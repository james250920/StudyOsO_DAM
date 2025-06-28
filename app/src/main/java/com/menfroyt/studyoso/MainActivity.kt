package com.menfroyt.studyoso

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.work.WorkManager
import com.menfroyt.studyoso.utils.PermissionHelper
import com.menfroyt.studyoso.utils.WorkManagerHelper
import com.menfroyt.studyoso.ui.theme.StudyOsoTheme
import com.menfroyt.studyoso.navigation.NavegacionApp
import com.menfroyt.studyoso.navigation.NavigationConstants
import com.menfroyt.studyoso.presentation.auth.SessionManager


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
                val sessionManager = remember { SessionManager(this) }
                val userId = remember { sessionManager.getUserId() }

                val initialScreen = remember {
                    if (intent?.getStringExtra(NavigationConstants.EXTRA_SCREEN) == NavigationConstants.SCREEN_LIST_TASK) {
                        "direct_tasks/$userId" // Usar el userId guardado
                    } else {
                        if (sessionManager.isLoggedIn()) {
                            "home/$userId"
                        } else {
                            "landing"
                        }
                    }
                }
                NavegacionApp(startDestination = initialScreen)
            }
        }
    }

    private fun setupWorkManager() {
        // Cancelar trabajos anteriores
        WorkManager.getInstance(this).cancelAllWorkByTag("verificacion_tareas")

        // Solo configurar WorkManager si hay sesión activa
        val sessionManager = SessionManager(this)
        if (sessionManager.isLoggedIn()) {
            workManagerHelper = WorkManagerHelper(this)
            workManagerHelper.verificarTareasVencidas()
        }
    }

    companion object {
        const val MESSAGE_STATUS = "message_status"
    }
    override fun onResume() {
        super.onResume()
        // Verificar estado de sesión cuando la app vuelve al primer plano
        setupWorkManager()
    }
}
