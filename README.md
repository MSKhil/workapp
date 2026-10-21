# WorkApp

Концепт корпоративного мобильного портала, написанный на Kotlin Multiplatform.
Проект создавался для демонстрации единой кодовой базы между Android и WebAssembly (Wasm) с использованием Compose Multiplatform.

## Стек технологий

* **Core:** Kotlin Multiplatform
* **UI:** Compose Multiplatform
* **Архитектура:** MVI (на базе библиотеки Voyager: Navigator + ScreenModel)
* **Сеть:** Ktor Client + kotlinx.serialization
* **Хранилище:** Multiplatform Settings (Key-Value)
* **Android-специфика:** Glance AppWidget (Jetpack) для виджетов домашнего экрана

## Архитектурные решения и особенности

1. **Единый UI без потери FPS в WebAssembly**
   Вместо тяжелых нативных `RenderEffect` (которые убивают производительность в Skia/Wasm), эффект Glassmorphism реализован через математику слоев: `drawBehind` и маскирование `BlendMode.DstIn`. Это дает стабильные 60-120 fps на обеих платформах при скролле сложных списков.

2. **Offline-first подход**
   Реализовано кэширование сетевых ответов (расписание, метаданные) локально. Приложение сохраняет работоспособность при отсутствии сети, информируя пользователя об использовании кэша.

3. **Mock-Backend**
   Для упрощения развертывания портфолио-проекта роль бэкенда выполняет Google Apps Script, который отдает статический JSON. Это избавляет от необходимости поднимать и поддерживать отдельный сервер.

4. **Платформенный код (expect/actual)**
   Общая бизнес-логика шарится на 90%. Через `expect/actual` вынесены только специфичные для платформ вещи (работа с Date/Time API, заглушки системных интентов).

## Структура модулей

* `:shared` — ядро приложения. Содержит бизнес-логику, сетевой слой, локальное хранилище и весь UI на Compose.
* `:androidApp` — точка входа для Android. Содержит манифест, настройки Splash-скрина и реализацию Glance Widget.
* `:webApp` — точка входа для Wasm. Содержит конфигурацию Canvas и HTML-обертку.

## Как запустить

### Android
1. Откройте проект в Android Studio (рекомендуется версия Ladybug или новее).
2. Выберите конфигурацию запуска `androidApp`.
3. Запустите на эмуляторе или реальном устройстве.

### Web (Wasm)
Сборка не требует Node.js или других сторонних зависимостей.
1. В корне проекта выполните сборку статики:
   ```bash
   ./gradlew :webApp:wasmJsBrowserDistribution
2. Скомпилированные файлы (index.html, .wasm, .js) появятся в директории:
   ```bash
   webApp/build/dist/wasmJs/productionExecutable/
3. Запустите любой локальный http-сервер в этой папке. Например, через Python:
   ```bash
   cd webApp/build/dist/wasmJs/productionExecutable/
   python3 -m http.server 8080
4. Откройте http://localhost:8080 в браузере (требуется браузер с поддержкой Wasm GC: Chrome/Edge 119+, Firefox 120+, Safari 17.4+).