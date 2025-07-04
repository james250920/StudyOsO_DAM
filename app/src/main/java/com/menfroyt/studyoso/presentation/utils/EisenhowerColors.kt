package com.menfroyt.studyoso.presentation.utils

import androidx.compose.ui.graphics.Color

/**
 * Utilidad para manejar los colores de la Matriz de Eisenhower
 * de manera consistente en toda la aplicación
 */
object EisenhowerColors {
    
    // Colores principales de cada cuadrante
    val UrgenteImportante = Color(0xFFFF6B6B)      // Rojo - Hacer Ahora
    val NoUrgenteImportante = Color(0xFF4ECDC4)    // Verde azulado - Planificar
    val UrgenteNoImportante = Color(0xFFFFBE0B)    // Amarillo - Delegar
    val NoUrgenteNoImportante = Color(0xFF95A5A6)  // Gris - Eliminar
    
    /**
     * Obtiene el color correspondiente según importancia y urgencia
     */
    fun getColorForTask(esImportante: Boolean, esUrgente: Boolean): Color {
        return when {
            esImportante && esUrgente -> UrgenteImportante
            esImportante && !esUrgente -> NoUrgenteImportante
            !esImportante && esUrgente -> UrgenteNoImportante
            else -> NoUrgenteNoImportante
        }
    }
    
    /**
     * Obtiene el color de fondo con transparencia para tarjetas
     */
    fun getBackgroundColor(esImportante: Boolean, esUrgente: Boolean, alpha: Float = 0.1f): Color {
        return getColorForTask(esImportante, esUrgente).copy(alpha = alpha)
    }
    
    /**
     * Obtiene el color del borde
     */
    fun getBorderColor(esImportante: Boolean, esUrgente: Boolean, alpha: Float = 0.3f): Color {
        return getColorForTask(esImportante, esUrgente).copy(alpha = alpha)
    }
    
    /**
     * Obtiene el título del cuadrante según importancia y urgencia
     */
    fun getTitleForQuadrant(esImportante: Boolean, esUrgente: Boolean): String {
        return when {
            esImportante && esUrgente -> "Hacer Ahora"
            esImportante && !esUrgente -> "Planificar"
            !esImportante && esUrgente -> "Delegar"
            else -> "Eliminar"
        }
    }
    
    /**
     * Obtiene la descripción del cuadrante según importancia y urgencia
     */
    fun getDescriptionForQuadrant(esImportante: Boolean, esUrgente: Boolean): String {
        return when {
            esImportante && esUrgente -> "Urgente e Importante"
            esImportante && !esUrgente -> "No Urgente e Importante"
            !esImportante && esUrgente -> "Urgente y No Importante"
            else -> "No Urgente y No Importante"
        }
    }
    
    /**
     * Obtiene el nivel de prioridad como texto
     */
    fun getPriorityText(esImportante: Boolean, esUrgente: Boolean): String {
        return when {
            esImportante && esUrgente -> "Prioridad Alta"
            esImportante && !esUrgente -> "Prioridad Media-Alta"
            !esImportante && esUrgente -> "Prioridad Media"
            else -> "Prioridad Baja"
        }
    }
}
