tracker-android/
├── .github/
│   └── workflows/
│       └── ci.yml
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/jaydeep/tracker/
│   │   │   │   ├── TrackerApp.kt                          # Hilt Application class
│   │   │   │   ├── MainActivity.kt                        # Single activity host
│   │   │   │   │
│   │   │   │   ├── core/
│   │   │   │   │   ├── data/
│   │   │   │   │   │   ├── local/
│   │   │   │   │   │   │   ├── TrackerDatabase.kt         # Room DB
│   │   │   │   │   │   │   ├── dao/
│   │   │   │   │   │   │   │   ├── ExpenseDao.kt
│   │   │   │   │   │   │   │   └── ProteinDao.kt
│   │   │   │   │   │   │   └── entities/
│   │   │   │   │   │   │       ├── ExpenseEntity.kt
│   │   │   │   │   │   │       └── ProteinEntity.kt
│   │   │   │   │   │   ├── remote/
│   │   │   │   │   │   │   ├── api/
│   │   │   │   │   │   │   │   ├── AuthApi.kt
│   │   │   │   │   │   │   │   ├── ExpenseApi.kt
│   │   │   │   │   │   │   │   ├── ProteinApi.kt
│   │   │   │   │   │   │   │   ├── SummaryApi.kt
│   │   │   │   │   │   │   │   └── UserApi.kt
│   │   │   │   │   │   │   └── dto/
│   │   │   │   │   │   │       ├── AuthDto.kt             # LoginRequest, AuthResponse, RefreshRequest
│   │   │   │   │   │   │       ├── ExpenseDto.kt          # Expense, ExpenseRequest
│   │   │   │   │   │   │       ├── ProteinDto.kt          # ProteinEntry, ProteinRequest
│   │   │   │   │   │   │       ├── SummaryDto.kt          # SummaryResponse
│   │   │   │   │   │   │       └── UserDto.kt             # UserResponse
│   │   │   │   │   │   └── repository/
│   │   │   │   │   │       ├── AuthRepository.kt
│   │   │   │   │   │       ├── ExpenseRepository.kt
│   │   │   │   │   │       ├── ProteinRepository.kt
│   │   │   │   │   │       ├── SummaryRepository.kt
│   │   │   │   │   │       └── UserRepository.kt
│   │   │   │   │   │
│   │   │   │   │   ├── di/
│   │   │   │   │   │   ├── DatabaseModule.kt              # Room + DAO bindings
│   │   │   │   │   │   ├── NetworkModule.kt               # Retrofit + OkHttp + interceptors
│   │   │   │   │   │   ├── RepositoryModule.kt            # Repo bindings
│   │   │   │   │   │   └── SecurityModule.kt              # EncryptedSharedPreferences
│   │   │   │   │   │
│   │   │   │   │   ├── network/
│   │   │   │   │   │   ├── AuthAuthenticator.kt           # OkHttp Authenticator (401 → refresh → retry)
│   │   │   │   │   │   └── AuthInterceptor.kt             # Attaches Bearer token
│   │   │   │   │   │
│   │   │   │   │   ├── security/
│   │   │   │   │   │   └── TokenStore.kt                  # EncryptedSharedPrefs read/write
│   │   │   │   │   │
│   │   │   │   │   ├── sync/
│   │   │   │   │   │   └── SyncWorker.kt                  # WorkManager sync (last-write-wins)
│   │   │   │   │   │
│   │   │   │   │   └── ui/
│   │   │   │   │       ├── navigation/
│   │   │   │   │       │   ├── AppNavGraph.kt             # NavHost + all routes
│   │   │   │   │       │   └── Screen.kt                  # sealed class route definitions
│   │   │   │   │       └── theme/
│   │   │   │   │           ├── Color.kt
│   │   │   │   │           ├── Theme.kt
│   │   │   │   │           └── Type.kt
│   │   │   │   │
│   │   │   │   ├── feature/
│   │   │   │   │   ├── auth/
│   │   │   │   │   │   ├── ui/
│   │   │   │   │   │   │   ├── LoginScreen.kt
│   │   │   │   │   │   │   └── LoginViewModel.kt
│   │   │   │   │   │   └── domain/
│   │   │   │   │   │       └── LoginUseCase.kt
│   │   │   │   │   │
│   │   │   │   │   ├── dashboard/
│   │   │   │   │   │   └── ui/
│   │   │   │   │   │       ├── DashboardScreen.kt
│   │   │   │   │   │       └── DashboardViewModel.kt
│   │   │   │   │   │
│   │   │   │   │   ├── expenses/
│   │   │   │   │   │   ├── ui/
│   │   │   │   │   │   │   ├── ExpenseListScreen.kt
│   │   │   │   │   │   │   ├── ExpenseDetailScreen.kt
│   │   │   │   │   │   │   ├── ExpenseEditScreen.kt
│   │   │   │   │   │   │   └── ExpenseViewModel.kt
│   │   │   │   │   │   └── domain/
│   │   │   │   │   │       └── ExpenseUseCase.kt
│   │   │   │   │   │
│   │   │   │   │   ├── protein/
│   │   │   │   │   │   ├── ui/
│   │   │   │   │   │   │   ├── ProteinListScreen.kt
│   │   │   │   │   │   │   ├── ProteinEditScreen.kt
│   │   │   │   │   │   │   └── ProteinViewModel.kt
│   │   │   │   │   │   └── domain/
│   │   │   │   │   │       └── ProteinUseCase.kt
│   │   │   │   │   │
│   │   │   │   │   ├── summary/
│   │   │   │   │   │   └── ui/
│   │   │   │   │   │       ├── SummaryScreen.kt           # Charts (Vico or Canvas)
│   │   │   │   │   │       └── SummaryViewModel.kt
│   │   │   │   │   │
│   │   │   │   │   └── settings/
│   │   │   │   │       └── ui/
│   │   │   │   │           ├── SettingsScreen.kt          # logout, base URL override
│   │   │   │   │           └── SettingsViewModel.kt
│   │   │   │   │
│   │   │   │   └── util/
│   │   │   │       ├── DateUtils.kt                       # ISO parse/format helpers
│   │   │   │       ├── Result.kt                          # sealed Result<T>
│   │   │   │       └── Extensions.kt                      # Flow, coroutine helpers
│   │   │   │
│   │   │   ├── res/
│   │   │   │   ├── values/
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   └── themes.xml
│   │   │   │   └── drawable/
│   │   │   │       └── ic_launcher_foreground.xml
│   │   │   │
│   │   │   └── AndroidManifest.xml
│   │   │
│   │   ├── test/                                          # JUnit + MockK unit tests
│   │   │   └── java/com/jaydeep/tracker/
│   │   │       ├── ExpenseViewModelTest.kt
│   │   │       ├── ProteinViewModelTest.kt
│   │   │       └── AuthRepositoryTest.kt
│   │   │
│   │   └── androidTest/                                   # Compose UI test
│   │       └── java/com/jaydeep/tracker/
│   │           └── LoginFlowTest.kt
│   │
│   ├── build.gradle.kts                                   # App-level: deps, plugins
│   └── proguard-rules.pro
│
├── gradle/
│   └── libs.versions.toml                                 # Version catalog
├── build.gradle.kts                                       # Root build
├── settings.gradle.kts
├── openapi.yaml
└── README.md