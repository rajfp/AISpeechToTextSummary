package com.example.speechtotext

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.speechtotext.ui.theme.SpeechToTextTheme
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            SpeechToTextTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    VoiceNoteScreen(
                        modifier = Modifier.padding(innerPadding),
                        snackbarHostState = snackbarHostState
                    )
                }
            }
        }
    }
}

@Composable
fun VoiceNoteScreen(
    modifier: Modifier = Modifier,
    viewModel: VoiceNoteViewModel = viewModel(),
    snackbarHostState: SnackbarHostState
) {
    val transcribedText by viewModel.transcribedText.collectAsState()
    val summaryText by viewModel.summaryText.collectAsState()
    val isSummarizing by viewModel.isSummarizing.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val speechRecognitionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!results.isNullOrEmpty()) {
                viewModel.updateTranscribedText(results[0])
            }
        } else {
            // Optionally handle speech recognition failure/cancellation in ViewModel
            // viewModel.onSpeechRecognitionFailed()
        }
    }

    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            viewModel.onAudioPermissionResult(isGranted)
        }
    )

    LaunchedEffect(key1 = Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is VoiceNoteUiEvent.RequestRecordAudioPermission -> {
                    recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }

                is VoiceNoteUiEvent.LaunchSpeechToText -> {
                    launchSpeechRecognizer(context, speechRecognitionLauncher)
                }
            }
        }
    }

    LaunchedEffect(userMessage) {
        if (userMessage.isNotBlank()) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(userMessage)
                viewModel.clearUserMessage()
            }
        }
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        OutlinedTextField(
            value = transcribedText,
            onValueChange = { viewModel.updateTranscribedText(it) }, // Allow editing for now
            label = { Text("Your voice note") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f),
            readOnly = false // Or set based on a ViewModel state if needed
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = if (isSummarizing) "Summarizing..." else summaryText,
            onValueChange = { /* Summary is usually not editable by user */ },
            label = { Text("Summary") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f),
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(onClick = {
                when (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                )) {
                    PackageManager.PERMISSION_GRANTED -> {
                        launchSpeechRecognizer(context, speechRecognitionLauncher)
                    }

                    else -> {
                        viewModel.requestRecordAudioPermission()
                    }
                }
            }) {
                Icon(Icons.Filled.Mic, contentDescription = "Start recording")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Record")
            }

            Button(onClick = { viewModel.clearAllText() }) {
                Icon(Icons.Filled.Clear, contentDescription = "Clear text")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Clear All")
            }

            Button(
                onClick = { viewModel.summarizeText() },
                enabled = transcribedText.isNotBlank() && !isSummarizing
            ) {
                Text("Summary")
            }
        }
    }
}

private fun launchSpeechRecognizer(
    context: Context,
    launcher: ActivityResultLauncher<Intent>
) {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
    }
    if (intent.resolveActivity(context.packageManager) != null) {
        launcher.launch(intent)
    } else {
        Log.e("STT", "STT not available on this device.")
        // Consider sending an event to ViewModel to show this error in Snackbar
        // viewModel.postUserMessage("Speech to text not available on this device.")
    }
}

@Preview(showBackground = true)
@Composable
fun VoiceNoteScreenPreview() {
    SpeechToTextTheme {
        // Preview might need a dummy ViewModel or simplified setup if ViewModel has complex dependencies
        // For now, this might have issues if the default ViewModel() constructor needs specific setup.
        // A common practice for Previews with ViewModels is to pass a fake/mock ViewModel.
        val snackbarHostState = remember { SnackbarHostState() }
        VoiceNoteScreen(snackbarHostState = snackbarHostState) // This will use default VM from compose
    }
}
