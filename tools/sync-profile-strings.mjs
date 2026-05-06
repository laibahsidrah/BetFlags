import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const RES = path.join(__dirname, "../app/src/main/res");

const KEYS = [
  "tab_profile",
  "profile_saved_section",
  "profile_tools_section",
  "nav_my_profile",
  "screen_my_profile_title",
  "sidebar_my_profile",
  "top_hub_profile",
];

const EN = ["Profile", "Saved", "Tools", "Profile", "Profile", "Profile", "PROFILE"];

const OVERRIDES = {
  "values-ru": ["Профиль", "Избранное", "Инструменты", "Профиль", "Профиль", "Профиль", "ПРОФИЛЬ"],
  "values-uk": ["Профіль", "Збережене", "Інструменти", "Профіль", "Профіль", "Профіль", "ПРОФІЛЬ"],
  "values-be": ["Профіль", "Захаванае", "Інструменты", "Профіль", "Профіль", "Профіль", "ПРОФІЛЬ"],
  "values-de": ["Profil", "Gespeichert", "Tools", "Profil", "Profil", "Profil", "PROFIL"],
  "values-fr": ["Profil", "Enregistrés", "Outils", "Profil", "Profil", "Profil", "PROFIL"],
  "values-es": ["Perfil", "Guardado", "Herramientas", "Perfil", "Perfil", "Perfil", "PERFIL"],
  "values-it": ["Profilo", "Salvati", "Strumenti", "Profilo", "Profilo", "Profilo", "PROFILO"],
  "values-pt": ["Perfil", "Guardados", "Ferramentas", "Perfil", "Perfil", "Perfil", "PERFIL"],
  "values-pl": ["Profil", "Zapisane", "Narzędzia", "Profil", "Profil", "Profil", "PROFIL"],
  "values-tr": ["Profil", "Kaydedilenler", "Araçlar", "Profil", "Profil", "Profil", "PROFİL"],
  "values-ar": ["الملف الشخصي", "المحفوظات", "الأدوات", "الملف الشخصي", "الملف الشخصي", "الملف الشخصي", "ملفي"],
  "values-zh": ["个人资料", "已保存", "工具", "个人资料", "个人资料", "个人资料", "资料"],
  "values-ja": ["プロフィール", "保存済み", "ツール", "プロフィール", "プロフィール", "プロフィール", "プロフィール"],
  "values-ko": ["프로필", "저장됨", "도구", "프로필", "프로필", "프로필", "프로필"],
  "values-nl": ["Profiel", "Opgeslagen", "Tools", "Profiel", "Profiel", "Profiel", "PROFIEL"],
  "values-cs": ["Profil", "Uložené", "Nástroje", "Profil", "Profil", "Profil", "PROFIL"],
  "values-sk": ["Profil", "Uložené", "Nástroje", "Profil", "Profil", "Profil", "PROFIL"],
  "values-hu": ["Profil", "Mentett", "Eszközök", "Profil", "Profil", "Profil", "PROFIL"],
  "values-ro": ["Profil", "Salvate", "Instrumente", "Profil", "Profil", "Profil", "PROFIL"],
  "values-bg": ["Профил", "Запазено", "Инструменти", "Профил", "Профил", "Профил", "ПРОФИЛ"],
  "values-el": ["Προφίλ", "Αποθηκευμένα", "Εργαλεία", "Προφίλ", "Προφίλ", "Προφίλ", "ΠΡΟΦΙΛ"],
  "values-he": ["פרופיל", "שמורים", "כלים", "פרופיל", "פרופיל", "פרופיל", "פרופיל"],
  "values-hi": ["प्रोफ़ाइल", "सहेजा गया", "टूल", "प्रोफ़ाइल", "प्रोफ़ाइल", "प्रोफ़ाइल", "प्रोफ़ाइल"],
  "values-vi": ["Hồ sơ", "Đã lưu", "Công cụ", "Hồ sơ", "Hồ sơ", "Hồ sơ", "HỒ SƠ"],
  "values-th": ["โปรไฟล์", "บันทึกแล้ว", "เครื่องมือ", "โปรไฟล์", "โปรไฟล์", "โปรไฟล์", "โปรไฟล์"],
  "values-id": ["Profil", "Tersimpan", "Alat", "Profil", "Profil", "Profil", "PROFIL"],
  "values-ms": ["Profil", "Disimpan", "Alat", "Profil", "Profil", "Profil", "PROFIL"],
  "values-fil": ["Profile", "Naka-save", "Mga tool", "Profile", "Profile", "Profile", "PROFILE"],
  "values-sv": ["Profil", "Sparat", "Verktyg", "Profil", "Profil", "Profil", "PROFIL"],
  "values-da": ["Profil", "Gemt", "Værktøjer", "Profil", "Profil", "Profil", "PROFIL"],
  "values-fi": ["Profiili", "Tallennettu", "Työkalut", "Profiili", "Profiili", "Profiili", "PROFIILI"],
  "values-no": ["Profil", "Lagret", "Verktøy", "Profil", "Profil", "Profil", "PROFIL"],
  "values-lv": ["Profils", "Saglabāts", "Rīki", "Profils", "Profils", "Profils", "PROFILS"],
  "values-lt": ["Profilis", "Išsaugota", "Įrankiai", "Profilis", "Profilis", "Profilis", "PROFILIS"],
  "values-et": ["Profiil", "Salvestatud", "Tööriistad", "Profiil", "Profiil", "Profiil", "PROFIIL"],
  "values-hr": ["Profil", "Spremljeno", "Alati", "Profil", "Profil", "Profil", "PROFIL"],
  "values-bs": ["Profil", "Sačuvano", "Alati", "Profil", "Profil", "Profil", "PROFIL"],
  "values-sr": ["Профил", "Сачувано", "Алати", "Профил", "Профил", "Профил", "ПРОФИЛ"],
  "values-sl": ["Profil", "Shranjeno", "Orodja", "Profil", "Profil", "Profil", "PROFIL"],
  "values-sq": ["Profili", "Të ruajtura", "Mjetet", "Profili", "Profili", "Profili", "PROFILI"],
  "values-mk": ["Профил", "Зачувано", "Алатки", "Профил", "Профил", "Профил", "ПРОФИЛ"],
  "values-az": ["Profil", "Saxlanılmış", "Alətlər", "Profil", "Profil", "Profil", "PROFİL"],
  "values-ka": ["პროფილი", "შენახული", "ინსტრუმენტები", "პროფილი", "პროფილი", "პროფილი", "პროფილი"],
  "values-fa": ["پروفایل", "ذخیره‌شده", "ابزارها", "پروفایل", "پروفایل", "پروفایل", "پروفایل"],
  "values-ur": ["پروفائل", "محفوظ", "اوزار", "پروفائل", "پروفائل", "پروفائل", "پروفائل"],
  "values-bn": ["প্রোফাইল", "সংরক্ষিত", "টুল", "প্রোফাইল", "প্রোফাইল", "প্রোফাইল", "প্রোফাইল"],
  "values-ne": ["प्रोफाइल", "सुरक्षित", "उपकरणहरू", "प्रोफाइल", "प्रोफाइल", "प्रोफाइल", "प्रोफाइल"],
  "values-sw": ["Wasifu", "Imehifadhiwa", "Zana", "Wasifu", "Wasifu", "Wasifu", "WASIFU"],
  "values-am": ["መገለጫ", "የተቀመጠ", "መሳሪያዎች", "መገለጫ", "መገለጫ", "መገለጫ", "መገለጫ"],
  "values-uz": ["Profil", "Saqlangan", "Asboblar", "Profil", "Profil", "Profil", "PROFIL"],
  "values-kk": ["Профиль", "Сақталған", "Құралдар", "Профиль", "Профиль", "Профиль", "ПРОФИЛЬ"],
  "values-ky": ["Профиль", "Сакталган", "Куралдар", "Профиль", "Профиль", "Профиль", "ПРОФИЛЬ"],
  "values-tg": ["Профил", "Захирашуда", "Воситаҳо", "Профил", "Профил", "Профил", "ПРОФИЛ"],
  "values-mn": ["Профайл", "Хадгалсан", "Хэрэгслүүд", "Профайл", "Профайл", "Профайл", "ПРОФАЙЛ"],
};

function esc(s) {
  return s.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
}

function hasKey(raw, key) {
  return new RegExp(`<string\\s+name="${key}"`).test(raw);
}

function patchFile(filePath, folder) {
  let raw = fs.readFileSync(filePath, "utf8");
  const missing = KEYS.filter((k) => !hasKey(raw, k));
  if (missing.length === 0) return false;
  const t = OVERRIDES[folder] || EN;
  const lines = [""];
  KEYS.forEach((key, i) => {
    if (missing.includes(key)) {
      lines.push(`    <string name="${key}">${esc(t[i])}</string>`);
    }
  });
  const block = lines.join("\n") + "\n";
  if (!raw.includes("</resources>")) throw new Error("bad xml " + filePath);
  raw = raw.replace("</resources>", block + "</resources>");
  fs.writeFileSync(filePath, raw, "utf8");
  return true;
}

const dirs = fs.readdirSync(RES).filter((d) => d.startsWith("values"));
let n = 0;
for (const d of dirs.sort()) {
  const fp = path.join(RES, d, "strings.xml");
  if (!fs.existsSync(fp)) continue;
  if (patchFile(fp, d)) {
    console.log("patched", d);
    n++;
  }
}
console.log("total", n);
