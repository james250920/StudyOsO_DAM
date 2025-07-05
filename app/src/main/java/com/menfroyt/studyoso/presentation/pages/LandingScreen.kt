package com.menfroyt.studyoso.presentation.pages

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.menfroyt.studyoso.R
import kotlinx.coroutines.delay
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StudyOsoLandingScreen(
    navController: NavController
) {
    // Sistema de colores mejorado con mejor contraste y accesibilidad
    val primaryColor = Color(0xFF1565C0) // Azul más accesible
    val primaryVariant = Color(0xFF0D47A1)
    val secondaryColor = Color(0xFF42A5F5)
    val accentColor = Color(0xFF26C6DA)
    val backgroundColor = Color(0xFFF8FAFF)
    val surfaceColor = Color.White
    val onSurfaceVariant = Color(0xFF5F6368)
    val successColor = Color(0xFF4CAF50)
    
    // Estados para animaciones y carrusel
    var isVisible by remember { mutableStateOf(false) }
    var currentFeatureIndex by remember { mutableStateOf(0) }
    val animatedScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )
    
    val features = remember {
        listOf(
            FeatureItem(
                icon = Icons.Outlined.Schedule,
                title = "Planificación Inteligente",
                description = "Algoritmos avanzados que aprenden de tus patrones de estudio",
                highlight = "IA integrada"
            ),
            FeatureItem(
                icon = Icons.Outlined.CheckCircle,
                title = "Seguimiento de Tareas",
                description = "Notificaciones inteligentes y recordatorios adaptativos",
                highlight = "Nunca olvides nada"
            ),
            FeatureItem(
                icon = Icons.Outlined.EmojiEvents,
                title = "Análisis de Rendimiento",
                description = "Métricas detalladas y recomendaciones personalizadas",
                highlight = "Mejora continua"
            )
        )
    }
    
    // Animación de carrusel mejorada
    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
        while (true) {
            delay(5000) // Tiempo más largo para leer
            currentFeatureIndex = (currentFeatureIndex + 1) % features.size
        }
    }
    
    // Configuración responsive
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val screenWidth = configuration.screenWidthDp
    val isTablet = screenWidth >= 600
    
    // Gradiente de fondo más sutil
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            backgroundColor,
            Color.White,
            backgroundColor.copy(alpha = 0.3f)
        ),
        startY = 0f,
        endY = Float.POSITIVE_INFINITY
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush)
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = if (isTablet) 48.dp else 24.dp,
                vertical = if (isLandscape) 16.dp else 32.dp
            )
    ) {
        // Header con logo y título principal
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(800)) +
                    slideInVertically(
                        initialOffsetY = { -30 },
                        animationSpec = tween(800, easing = FastOutSlowInEasing)
                    )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (isLandscape) 24.dp else 40.dp)
            ) {
                // Badge de bienvenida
                Surface(
                    modifier = Modifier.padding(bottom = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = primaryColor.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Bienvenido a StudyOso",
                            style = MaterialTheme.typography.labelMedium,
                            color = primaryColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                // Título principal mejorado
                Text(
                    text = buildAnnotatedString {
                        append("Transforma tu ")
                        withStyle(
                            style = SpanStyle(
                                color = primaryColor,
                                fontWeight = FontWeight.ExtraBold
                            )
                        ) {
                            append("Vida Académica")
                        }
                    },
                    fontSize = if (isTablet) 32.sp else 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = if (isTablet) 40.sp else 36.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Subtítulo
                Text(
                    text = "La plataforma de estudio más avanzada para estudiantes",
                    style = MaterialTheme.typography.titleMedium,
                    color = onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }
        
        // Imagen/ilustración central con animación mejorada
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(1000, 400)) +
                    scaleIn(
                        initialScale = 0.8f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = if (isLandscape) 10.dp else 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.study),
                    contentDescription = "Ilustración de StudyOso",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(
                            width = if (isTablet) 280.dp else 240.dp,
                            height = if (isTablet) 200.dp else 170.dp
                        )
                        .scale(animatedScale)
                )
            }
        }
        
        // Sección de características con indicadores
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        ) {
            // Carrusel de características mejorado
            AnimatedContent(
                targetState = currentFeatureIndex,
                transitionSpec = {
                    fadeIn(animationSpec = tween(500)) + 
                    slideInHorizontally(
                        initialOffsetX = { width -> width / 3 }, 
                        animationSpec = tween(500, easing = FastOutSlowInEasing)
                    ) with
                    fadeOut(animationSpec = tween(300)) + 
                    slideOutHorizontally(
                        targetOffsetX = { width -> -width / 3 }, 
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) { index ->
                HorizontalFeatureCard(
                    feature = features[index],
                    primaryColor = primaryColor,
                    accentColor = accentColor
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Indicadores de características
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                features.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .size(if (index == currentFeatureIndex) 24.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == currentFeatureIndex) primaryColor 
                                else primaryColor.copy(alpha = 0.3f)
                            )
                            .clickable { currentFeatureIndex = index }
                            .animateContentSize()
                    )
                    if (index < features.size - 1) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(18.dp))
        
        // Sección de valor propuesto
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(1000, 800)) + 
                   expandVertically(animationSpec = tween(800))
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.School,
                        contentDescription = null,
                        tint = successColor,
                        modifier = Modifier.size(32.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Tu Compañero Académico",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "Desarrollada con las últimas " +
                               "tecnologías Android para ofrecerte una interfaz intuitiva, seguridad total de tus datos y " +
                               "una experiencia de aprendizaje más eficiente que nunca.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = onSurfaceVariant,
                        lineHeight = 24.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Call-to-Action mejorado
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(1000, 1200)) + 
                   slideInVertically(
                       initialOffsetY = { 40 },
                       animationSpec = spring(
                           dampingRatio = Spring.DampingRatioMediumBouncy,
                           stiffness = Spring.StiffnessMedium
                       )
                   )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLandscape && !isTablet) {
                    // Layout horizontal para landscape móvil
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        EnhancedActionButton(
                            text = "Comenzar Gratis",
                            isPrimary = true,
                            onClick = { navController.navigate("register") },
                            primaryColor = primaryColor,
                            modifier = Modifier.weight(1f)
                        )
                        
                        EnhancedActionButton(
                            text = "Iniciar Sesión",
                            isPrimary = false,
                            onClick = { navController.navigate("login") },
                            primaryColor = primaryColor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    // Layout vertical para portrait y tablets
                    EnhancedActionButton(
                        text = "Comenzar Gratis",
                        isPrimary = true,
                        onClick = { navController.navigate("register") },
                        primaryColor = primaryColor,
                        modifier = Modifier.fillMaxWidth(if (isTablet) 0.6f else 0.9f)
                    )
                    
                    EnhancedActionButton(
                        text = "Ya tengo cuenta",
                        isPrimary = false,
                        onClick = { navController.navigate("login") },
                        primaryColor = primaryColor,
                        modifier = Modifier.fillMaxWidth(if (isTablet) 0.6f else 0.9f)
                    )
                }

            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun HorizontalFeatureCard(
    feature: FeatureItem,
    primaryColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de la característica
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.2f),
                                accentColor.copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = feature.icon,
                    contentDescription = feature.title,
                    tint = primaryColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Texto de la característica
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = feature.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = primaryColor
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = feature.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF5F6368)
                )
            }
        }
    }
}

@Composable
fun EnhancedFeatureCard(
    feature: FeatureItem,
    primaryColor: Color,
    accentColor: Color,
    isTablet: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono mejorado con gradiente
            Box(
                modifier = Modifier
                    .size(if (isTablet) 80.dp else 72.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.2f),
                                accentColor.copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = feature.icon,
                    contentDescription = feature.title,
                    tint = primaryColor,
                    modifier = Modifier.size(if (isTablet) 36.dp else 32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Título de la característica
            Text(
                text = feature.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = if (isTablet) 22.sp else 20.sp
                ),
                color = primaryColor,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Highlight badge
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = accentColor.copy(alpha = 0.15f)
            ) {
                Text(
                    text = feature.highlight,
                    style = MaterialTheme.typography.labelMedium,
                    color = primaryColor,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Descripción mejorada
            Text(
                text = feature.description,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = if (isTablet) 16.sp else 15.sp,
                    lineHeight = if (isTablet) 24.sp else 22.sp
                ),
                color = Color(0xFF5F6368),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun EnhancedActionButton(
    text: String,
    isPrimary: Boolean,
    onClick: () -> Unit,
    primaryColor: Color,
    modifier: Modifier = Modifier
) {
    val buttonColors = if (isPrimary) {
        ButtonDefaults.buttonColors(
            containerColor = primaryColor,
            contentColor = Color.White
        )
    } else {
        ButtonDefaults.elevatedButtonColors(
            containerColor = Color.White,
            contentColor = primaryColor
        )
    }
    
    val animatedScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )
    
    if (isPrimary) {
        Button(
            onClick = onClick,
            colors = buttonColors,
            shape = RoundedCornerShape(16.dp),
            modifier = modifier
                .height(60.dp)
                .scale(animatedScale),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp,
                hoveredElevation = 6.dp
            ),
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
        ) {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = 0.5.sp
            )
        }
    } else {
        ElevatedButton(
            onClick = onClick,
            colors = buttonColors,
            shape = RoundedCornerShape(16.dp),
            modifier = modifier
                .height(60.dp)
                .scale(animatedScale),
            elevation = ButtonDefaults.elevatedButtonElevation(
                defaultElevation = 2.dp,
                pressedElevation = 6.dp,
                hoveredElevation = 4.dp
            ),
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = primaryColor.copy(alpha = 0.3f)
            )
        ) {
            Text(
                text = text,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                letterSpacing = 0.3.sp
            )
        }
    }
}

// Clase de datos mejorada para las características
data class FeatureItem(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val highlight: String
)


