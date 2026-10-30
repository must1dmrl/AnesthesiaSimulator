package com.example.anesthesiasimulator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF0B1118)
private val Panel = Color(0xFF151F2B)
private val Muted = Color(0xFF9AABBC)
private val Green = Color(0xFF55D99A)
private val Amber = Color(0xFFFFC857)
private val Red = Color(0xFFFF6B6B)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme(colorScheme = darkColorScheme()) { SimulatorApp() } }
    }
}

private data class Stage(
    val title: String,
    val subtitle: String,
    val action: String,
    val detail: String
)

private val stages = listOf(
    Stage("Preoperatif değerlendirme", "Hasta ameliyathaneye alındı. Kimlik, öykü, alerji ve başlangıç bulgularını değerlendir.", "Değerlendirmeyi tamamla", "Başlangıç değerlendirmesi kaydedildi. Simülasyon spinal anestezi aşamasına geçti."),
    Stage("Spinal anestezi", "Spinal uygulama sonrası duyusal blok seviyesi değerlendiriliyor.", "Blok seviyesini değerlendir", "Blok seviyesi planlanan cerrahi için yetersiz. Cerrahi ekiple iletişim ve güvenli anestezi planının yeniden değerlendirilmesi gerekiyor."),
    Stage("Genel anesteziye geçiş", "Spinal blok yetersiz. Hastanın durumu ve hava yolu planı yeniden değerlendiriliyor.", "Genel anestezi planını onayla", "Genel anesteziye geçiş planı eğitim senaryosunda onaylandı. Sıradaki aşama hava yolu yönetimi."),
    Stage("Hava yolu yönetimi", "İndüksiyon ve entübasyon aşaması. Ekipman, oksijenasyon ve doğrulama adımlarını kontrol et.", "Entübasyon kontrolünü tamamla", "Hava yolu kontrol listesi tamamlandı. Tüp yerinin uygun yöntemlerle doğrulanması ve sürekli izlem sürdürülmeli."),
    Stage("İntraoperatif takip", "Monitör değerlerini izle. Simülasyon bir hemodinamik değişiklik oluşturdu.", "Komplikasyonu değerlendir", "Değişiklik fark edildi. Önce hastayı ve ölçümün doğruluğunu değerlendir; ekip çağrısı ve neden odaklı yaklaşım gereklidir."),
    Stage("Operasyon sonu", "Cerrahi tamamlandı. Uyanma ve ameliyat sonrası değerlendirme aşamasına geç.", "Senaryoyu tamamla", "Senaryo tamamlandı. Performans özeti hazır.")
)

@Composable
fun SimulatorApp() {
    var stageIndex by remember { mutableIntStateOf(0) }
    var showIntro by remember { mutableStateOf(true) }
    var selected by remember { mutableStateOf("") }
    val stage = stages[stageIndex]

    Surface(modifier = Modifier.fillMaxSize(), color = Bg) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("ANESTHESIA", color = Green, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Text("SIMULATOR  ·  v0.1", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
                Surface(color = Color(0xFF183A30), shape = RoundedCornerShape(50)) {
                    Text("EĞİTİM MODU", color = Green, modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            OperatingRoomScene()

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                VitalCard("HR", if (stageIndex < 4) "78" else "108", "bpm", Green, Modifier.weight(1f))
                VitalCard("SpO₂", if (stageIndex < 4) "98" else "94", "%", if (stageIndex < 4) Green else Amber, Modifier.weight(1f))
                VitalCard("NIBP", if (stageIndex < 4) "118/76" else "92/58", "mmHg", if (stageIndex < 4) Green else Amber, Modifier.weight(1f))
                VitalCard("EtCO₂", if (stageIndex < 4) "36" else "32", "mmHg", Green, Modifier.weight(1f))
            }

            Column(modifier = Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(16.dp)).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("VAKA AKIŞI", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text("${stageIndex + 1} / ${stages.size}", color = Green, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                LinearProgressIndicator(progress = { (stageIndex + 1f) / stages.size }, modifier = Modifier.fillMaxWidth(), color = Green, trackColor = Color(0xFF2B3947))
                Text(stage.title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(stage.subtitle, color = Muted, fontSize = 14.sp, lineHeight = 20.sp)

                if (stageIndex == 4) {
                    Surface(color = Color(0xFF3B3020), shape = RoundedCornerShape(10.dp)) {
                        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("⚠  Monitörde değişiklik: hemodinamik instabilite senaryosu", color = Amber, fontSize = 12.sp)
                        }
                    }
                }
                Button(
                    onClick = {
                        selected = stage.detail
                        if (stageIndex < stages.lastIndex) stageIndex++
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Color(0xFF07140F)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (stageIndex == stages.lastIndex) "Senaryoyu yeniden başlat" else stage.action, fontWeight = FontWeight.Bold)
                }
                if (stageIndex == stages.lastIndex) {
                    OutlinedButton(onClick = { stageIndex = 0; selected = "" }, modifier = Modifier.fillMaxWidth()) { Text("Baştan başla") }
                }
                if (selected.isNotBlank()) {
                    Text("GERİ BİLDİRİM", color = Green, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(selected, color = Color.White, fontSize = 13.sp, lineHeight = 19.sp)
                }
            }

            Text("Bu prototip eğitim amaçlıdır; gerçek hasta yönetimi veya klinik karar desteği için kullanılamaz.", color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
        }
    }
}

@Composable
private fun OperatingRoomScene() {
    Box(
        modifier = Modifier.fillMaxWidth().height(210.dp)
            .background(Color(0xFF1C2B37), RoundedCornerShape(18.dp))
            .border(1.dp, Color(0xFF344958), RoundedCornerShape(18.dp))
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width; val h = size.height
            // Back wall and floor perspective
            drawRect(Color(0xFF243744), size = size)
            val floor = Path().apply {
                moveTo(0f, h * .70f); lineTo(w, h * .70f); lineTo(w, h); lineTo(0f, h); close()
            }
            drawPath(floor, Color(0xFF17232D))
            // Ceiling light
            drawRoundRect(Color(0xFFB9C8CF), Offset(w*.38f, h*.08f), androidx.compose.ui.geometry.Size(w*.25f, h*.07f), androidx.compose.ui.geometry.CornerRadius(14f))
            drawLine(Color(0xFF8298A5), Offset(w*.505f,h*.15f), Offset(w*.505f,h*.29f), 5f)
            // Operating table
            drawRoundRect(Color(0xFF607986), Offset(w*.20f,h*.60f), androidx.compose.ui.geometry.Size(w*.60f,h*.12f), androidx.compose.ui.geometry.CornerRadius(12f))
            drawRect(Color(0xFF425966), Offset(w*.28f,h*.71f), androidx.compose.ui.geometry.Size(w*.035f,h*.20f))
            drawRect(Color(0xFF425966), Offset(w*.70f,h*.71f), androidx.compose.ui.geometry.Size(w*.035f,h*.20f))
            // Patient body, head and blanket
            drawOval(Color(0xFFD7A98E), Offset(w*.29f,h*.48f), androidx.compose.ui.geometry.Size(w*.10f,h*.12f))
            drawRoundRect(Color(0xFF4D91A1), Offset(w*.39f,h*.48f), androidx.compose.ui.geometry.Size(w*.32f,h*.13f), androidx.compose.ui.geometry.CornerRadius(18f))
            drawRoundRect(Color(0xFF397080), Offset(w*.53f,h*.49f), androidx.compose.ui.geometry.Size(w*.18f,h*.12f), androidx.compose.ui.geometry.CornerRadius(12f))
            // Monitor
            drawRoundRect(Color(0xFF080E13), Offset(w*.78f,h*.28f), androidx.compose.ui.geometry.Size(w*.16f,h*.24f), androidx.compose.ui.geometry.CornerRadius(8f))
            val ecg = Path().apply {
                moveTo(w*.795f,h*.40f); lineTo(w*.82f,h*.40f); lineTo(w*.835f,h*.35f)
                lineTo(w*.85f,h*.46f); lineTo(w*.865f,h*.39f); lineTo(w*.92f,h*.39f)
            }
            drawPath(ecg, Green, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))
            // Anesthesia machine silhouette
            drawRoundRect(Color(0xFF9AAEB8), Offset(w*.06f,h*.34f), androidx.compose.ui.geometry.Size(w*.12f,h*.27f), androidx.compose.ui.geometry.CornerRadius(8f))
            drawRoundRect(Color(0xFF17232D), Offset(w*.075f,h*.37f), androidx.compose.ui.geometry.Size(w*.09f,h*.08f), androidx.compose.ui.geometry.CornerRadius(4f))
            drawLine(Color(0xFF9AAEB8), Offset(w*.12f,h*.61f), Offset(w*.12f,h*.82f), 5f)
        }
        Column(Modifier.align(Alignment.TopStart).padding(12.dp)) {
            Text("OR 03  ·  OPERATING ROOM", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Text("3D SAHNE ÖNİZLEMESİ", color = Muted, fontSize = 9.sp)
        }
        Text("Hasta: Vaka 001", modifier = Modifier.align(Alignment.BottomStart).padding(12.dp), color = Color.White, fontSize = 11.sp)
    }
}

@Composable
private fun VitalCard(label: String, value: String, unit: String, color: Color, modifier: Modifier = Modifier) {
    Column(modifier.background(Panel, RoundedCornerShape(12.dp)).padding(vertical = 12.dp, horizontal = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(unit, color = Muted, fontSize = 9.sp)
    }
}
