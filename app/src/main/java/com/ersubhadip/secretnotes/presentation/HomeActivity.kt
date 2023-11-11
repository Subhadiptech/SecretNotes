package com.ersubhadip.secretnotes.presentation

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ersubhadip.secretnotes.R
import com.ersubhadip.secretnotes.biometric.BiometricHelper
import com.ersubhadip.secretnotes.ui.theme.SecretNotesTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SecretNotesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current

                    val viewModel: HomeViewModel = viewModel()

                    val notes by viewModel.notes.collectAsState()

                    var dialogOpenState by remember {
                        mutableStateOf(false)
                    }

                    var authorized by remember {
                        mutableStateOf(false)
                    }

                    OnLifecycleEvent { owner, event ->
                        if (event == Lifecycle.Event.ON_PAUSE) {
                            authorized = false
                        }
                    }

                    val authorize: () -> Unit = {
                        BiometricHelper.showPrompt(this) {
                            authorized = true
                        }
                    }

                    val blurValue by animateDpAsState(
                        targetValue = if (authorized) 0.dp else 15.dp,
                        animationSpec = tween(500),
                        label = "BlurAnimation"
                    )

                    var lockedBitmap: Bitmap? by remember { mutableStateOf(null) }

                    LaunchedEffect(key1 = Unit) {
                        authorize()
                    }

                    if (dialogOpenState) {
                        Dialog(onDismissRequest = { dialogOpenState = false }) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.primary)
                                        .padding(16.dp)
                                ) {
                                    var secretNote by remember {
                                        mutableStateOf("")
                                    }
                                    OutlinedTextField(value = secretNote, onValueChange = {
                                        secretNote = it
                                    }, modifier = Modifier.fillMaxWidth(), label = {
                                        Text(
                                            text = "Secret Notes",
                                            fontFamily = FontFamily(
                                                Font(R.font.ubuntu)
                                            )
                                        )
                                    },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = null
                                            )
                                        }, colors = TextFieldDefaults.outlinedTextFieldColors(
                                            focusedBorderColor = Color.White,
                                            focusedLeadingIconColor = Color.White,
                                            focusedLabelColor = Color.White,
                                            unfocusedLabelColor = Color(0xffcccccc),
                                            unfocusedLeadingIconColor = Color(0xffcccccc),
                                            unfocusedBorderColor = Color(0xffcccccc),
                                            textColor = Color.White
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = {
                                            if (secretNote.isNotEmpty()) {
                                                viewModel.createOne(secretNote)
                                                dialogOpenState = false
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.secondary
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = "Add Note",
                                            color = Color.White,
                                            fontFamily = FontFamily(
                                                Font(R.font.ubuntu)
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Scaffold(modifier = Modifier.fillMaxSize(), floatingActionButton = {
                        Column {
                            AnimatedVisibility(visible = !authorized) {
                                FloatingActionButton(
                                    onClick = authorize,
                                    containerColor = MaterialTheme.colorScheme.primary
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            FloatingActionButton(
                                onClick = {
                                    if (authorized) {
                                        dialogOpenState = true
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Unlock app to add any secret note.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                containerColor = MaterialTheme.colorScheme.primary
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }
                    }) { paddings ->

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddings)
                        ) {
                            if (authorized) {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    item {
                                        Spacer(modifier = Modifier.height(24.dp))
                                        Text(
                                            text = "My Secret Notes",
                                            color = Color.White,
                                            fontSize = 24.sp
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                    }
                                    if (notes.isNotEmpty()) {
                                        items(notes.reversed(), key = { it.id ?: 0 }) {
                                            Box(
                                                modifier = Modifier
                                                    .animateItemPlacement(
                                                        animationSpec = tween(
                                                            500
                                                        )
                                                    )
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(MaterialTheme.colorScheme.primary)
                                                    .padding(16.dp)

                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(text = it.note, color = Color.White)
                                                    AnimatedVisibility(visible = authorized) {
                                                        Icon(
                                                            imageVector = Icons.Default.Delete,
                                                            contentDescription = null,
                                                            tint = Color.White,
                                                            modifier = Modifier.clickable {
                                                                viewModel.deleteOne(it)
                                                            })
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        item {
                                            Text(
                                                text = "You have not added any notes yet",
                                                color = Color.White,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            } else {
                                Image(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    painter = painterResource(R.drawable.ic_blur_placeholder),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnLifecycleEvent(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
    val eventHandler = rememberUpdatedState(newValue = onEvent)
    val lifecycleOwner = rememberUpdatedState(newValue = LocalLifecycleOwner.current)

    DisposableEffect(key1 = lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { owner, event ->
            eventHandler.value(owner, event)
        }
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}