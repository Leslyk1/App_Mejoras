package com.example.clase7

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformesScreen(navController: NavController) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val scope = rememberCoroutineScope()
    val activity = LocalView.current.context as Activity

    var curso by remember { mutableStateOf("") }
    var año by remember { mutableStateOf("") }
    var semestre by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var comentarios by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Nuevo Informe",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {

            Text(
                text = "Crear Informe Académico",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0066B3),
                modifier = Modifier.padding(bottom = 20.dp)
            )

            if (message.isNotEmpty()) {
                Text(
                    text = message,
                    color = when {
                        message.contains("éxito") -> Color.Green
                        message.contains("Error") -> Color.Red
                        else -> Color.Black
                    },
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }


            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                OutlinedTextField(
                    value = curso,
                    onValueChange = { curso = it },
                    label = { Text("Curso *") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Create,
                            contentDescription = "Curso"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ej: Programación") }
                )


                OutlinedTextField(
                    value = año,
                    onValueChange = { año = it },
                    label = { Text("Año *") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Create,
                            contentDescription = "Año"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("Ej: 2025") }
                )


                OutlinedTextField(
                    value = semestre,
                    onValueChange = { semestre = it },
                    label = { Text("Semestre *") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Create,
                            contentDescription = "Semestre"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ej: Segundo") }
                )


                OutlinedTextField(
                    value = fecha,
                    onValueChange = { fecha = it },
                    label = { Text("Fecha") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Create,
                            contentDescription = "Fecha"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("DD/MM/AAAA") }
                )


                OutlinedTextField(
                    value = comentarios,
                    onValueChange = { comentarios = it },
                    label = { Text("Comentarios") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Create,
                            contentDescription = "Comentarios"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = { Text("Informe de la clase de programación...") },
                    singleLine = false
                )


                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Archivos Adjuntos",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Button(
                            onClick = {
                                Toast.makeText(context, "Funcionalidad de archivos próximamente", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0066B3)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Adjuntar archivo"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Seleccionar Archivos")
                        }

                        Text(
                            text = "Puedes adjuntar documentos, imágenes, etc.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }


            Button(
                onClick = {

                    if (curso.isEmpty() || año.isEmpty() || semestre.isEmpty()) {
                        message = "Error: Los campos con  son obligatorios"
                        return@Button
                    }

                    isLoading = true
                    message = "Guardando informe..."

                    scope.launch {
                        try {
                            guardarInforme(
                                curso = curso,
                                año = año,
                                semestre = semestre,
                                fecha = fecha,
                                comentarios = comentarios,
                                db = db,
                                onSuccess = {
                                    isLoading = false
                                    message = " Informe guardado exitosamente!"
                                    Toast.makeText(context, "Informe guardado", Toast.LENGTH_SHORT).show()


                                    curso = ""
                                    año = ""
                                    semestre = ""
                                    fecha = ""
                                    comentarios = ""
                                },
                                onError = { error ->
                                    isLoading = false
                                    message = " Error: $error"
                                    Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
                                }
                            )
                        } catch (e: Exception) {
                            isLoading = false
                            message = " Error: ${e.message}"
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFC9252B)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Guardar Informe", fontSize = 16.sp)
                }
            }
        }
    }
}


private fun guardarInforme(
    curso: String,
    año: String,
    semestre: String,
    fecha: String,
    comentarios: String,
    db: FirebaseFirestore,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {

    val user = FirebaseAuth.getInstance().currentUser
    if (user == null) {
        onError("Debe iniciar sesión para guardar informes")
        return
    }

    val informeData = hashMapOf(
        "curso" to curso,
        "año" to año,
        "semestre" to semestre,
        "fecha" to fecha,
        "comentarios" to comentarios,
        "archivos" to listOf<String>(),
        "fechaCreacion" to Timestamp.now(),
        "estado" to "pendiente",

        "usuarioId" to user.uid,
        "usuarioEmail" to user.email
    )

    db.collection("informes")
        .add(informeData)
        .addOnSuccessListener {
            onSuccess()
        }
        .addOnFailureListener { e ->

            val errorMsg = when {
                e.message?.contains("PERMISSION_DENIED") == true ->
                    "ERROR DE PERMISOS: Cambia las reglas de Firestore para permitir escritura"
                e.message?.contains("network") == true ->
                    "ERROR DE RED: Verifica tu conexión a internet"
                e.message?.contains("FirebaseApp") == true ->
                    "ERROR DE CONFIGURACIÓN: Verifica la configuración de Firebase"
                else -> "Error: ${e.message ?: "Error desconocido"}"
            }
            onError(errorMsg)
        }
}