package com.menfroyt.studyoso

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.menfroyt.studyoso.utils.PermissionHelper
import com.menfroyt.studyoso.utils.WorkManagerHelper
import com.menfroyt.studyoso.ui.theme.StudyOsoTheme
import com.menfroyt.studyoso.navigation.NavegacionApp


class MainActivity : ComponentActivity() {
    private lateinit var workManagerHelper: WorkManagerHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Solicitar permiso de notificaciones
        if (!PermissionHelper.checkNotificationPermission(this)) {
            PermissionHelper.requestNotificationPermission(this) { isGranted ->
                if (isGranted) {
                    // Inicializar WorkManager ahora que tenemos permiso
                    setupWorkManager()
                }
            }
        } else {
            // Ya tenemos permiso, inicializar WorkManager
            setupWorkManager()
        }

        setContent {
            StudyOsoTheme {
                val textInput = remember { mutableStateOf("") }
                NavegacionApp()
            }
        }
    }

    private fun setupWorkManager() {
        // Inicializar WorkManagerHelper
        workManagerHelper = WorkManagerHelper(this)
        // Iniciar verificación de tareas vencidas
        workManagerHelper.verificarTareasVencidas()
    }

    companion object {
        const val MESSAGE_STATUS = "message_status"
    }
}
