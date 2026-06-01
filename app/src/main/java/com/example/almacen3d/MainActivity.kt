package com.example.almacen3d

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.webkit.WebViewAssetLoader
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import com.example.almacen3d.model.ActiveSession
import com.example.almacen3d.model.Product
import com.example.almacen3d.model.Shelf
import com.example.almacen3d.network.WarehouseApiClient
import com.example.almacen3d.network.WarehouseRepository
import com.example.almacen3d.ui.CardFactory
import com.example.almacen3d.ui.StatusKind
import com.example.almacen3d.ui.cleanText
import com.example.almacen3d.ui.dp
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private val apiClient = WarehouseApiClient()
    private val repository = WarehouseRepository(apiClient)
    private val cardFactory by lazy { CardFactory(this) }
    private val executor = Executors.newSingleThreadExecutor()
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private val preferences: SharedPreferences by lazy {
        getSharedPreferences("almacen_mobile_cache", MODE_PRIVATE)
    }
    
    private lateinit var mobileDashboard: View
    private lateinit var loginScreen: View
    private lateinit var route3dContainer: View
    private lateinit var routeWebView: WebView
    private lateinit var scannerScreen: View
    private lateinit var scannerPreview: PreviewView
    private lateinit var appScreens: List<View>
    
    private var barcodeScanner: BarcodeScanner? = null
    private var products: List<Product> = emptyList()
    private var shelves: List<Shelf> = emptyList()
    private var editingProductSku: String? = null
    private var selectedRouteProduct: Product? = null
    private var productFilter: ProductFilter = ProductFilter.ALL
    private var scanOriginId: Int = R.id.mobileDashboard
    private var currentPageIndex = 0
    private var currentUserRole: String = "consulta"
    private var routeTokenInjectedForUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupProfessionalNativeLook()
        setContentView(R.layout.activity_main)
        bindViews()
        setupNativeFeatures()
        setupDashboard()
        setupRouteWebView()
        setupBackNavigation()
        setupWindowInsets()
        restoreSessionOrShowLogin()
    }

    private fun setupProfessionalNativeLook() {
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
    }

    private fun setupNativeFeatures() {
        createNotificationChannel()
        requestNotificationPermissionIfNeeded()
    }

    private fun bindViews() {
        loginScreen = findViewById(R.id.loginScreen)
        mobileDashboard = findViewById(R.id.mobileDashboard)
        route3dContainer = findViewById(R.id.route3dContainer)
        routeWebView = findViewById(R.id.routeWebView)
        scannerScreen = findViewById(R.id.scannerScreen)
        scannerPreview = findViewById(R.id.scannerPreview)
        appScreens = listOf(
            loginScreen,
            mobileDashboard,
            findViewById(R.id.productsScreen),
            findViewById(R.id.productFormScreen),
            findViewById(R.id.productRouteScreen),
            findViewById(R.id.shelvesScreen),
            findViewById(R.id.reportsScreen),
            findViewById(R.id.profileScreen),
            findViewById(R.id.settingsScreen),
            route3dContainer,
            scannerScreen
        )
    }

    private fun setupDashboard() {
        findViewById<View>(R.id.iniciarOperacionButton)?.setOnClickListener {
            hapticFeedback()
            findViewById<EditText>(R.id.homeSearchInput).requestFocus()
        }
        setupBottomNavigation()
        setupMobileScreens()
    }

    private fun applyMobileProductOnlyMode() { }



    private fun setupBottomNavigation() {
        findViewById<View>(R.id.navHomeButton).setOnClickListener { hapticFeedback(); showMobileDashboard(); updateBottomNav(R.id.navHomeButton) }
        findViewById<View>(R.id.navProductsButton).setOnClickListener { hapticFeedback(); showScreen(R.id.productsScreen); loadProducts(); updateBottomNav(R.id.navProductsButton) }
        findViewById<View>(R.id.navShelvesButton).setOnClickListener { hapticFeedback(); showScreen(R.id.shelvesScreen); loadShelves(); updateBottomNav(R.id.navShelvesButton) }
        findViewById<View>(R.id.navReportsButton).setOnClickListener { hapticFeedback(); showScreen(R.id.profileScreen); loadProfile(); updateBottomNav(R.id.navReportsButton) }
    }

    private fun updateBottomNav(activeId: Int) {
        val navIds = listOf(R.id.navHomeButton, R.id.navProductsButton, R.id.navShelvesButton, R.id.navReportsButton)
        navIds.forEach { id ->
            val view = findViewById<LinearLayout>(id)
            val icon = view.getChildAt(0) as ImageView
            val text = view.getChildAt(1) as TextView
            val isActive = id == activeId
            
            icon.setColorFilter(ContextCompat.getColor(this, if (isActive) R.color.secondary else R.color.warehouse_text_muted))
            text.setTextColor(ContextCompat.getColor(this, if (isActive) R.color.secondary else R.color.warehouse_text_muted))
            text.setTypeface(null, if (isActive) Typeface.BOLD else Typeface.NORMAL)
        }
    }

    private fun setupMobileScreens() {
        val backButtons = listOf(
            R.id.productsBackButton, R.id.productFormBackButton,
            R.id.productRouteBackButton, R.id.shelvesBackButton,
            R.id.reportsBackButton, R.id.profileBackButton, R.id.settingsBackButton
        )
        backButtons.forEach { buttonId ->
            findViewById<View>(buttonId).setOnClickListener { hapticFeedback(); showMobileDashboard() }
        }

        findViewById<View>(R.id.dashboardMenuButton)?.setOnClickListener { hapticFeedback(); showScreen(R.id.profileScreen); loadProfile() }
        findViewById<View>(R.id.dashboardNotifyButton)?.setOnClickListener { hapticFeedback(); showAppMessage("No tienes notificaciones pendientes") }
        findViewById<View>(R.id.homeVerTodosButton)?.setOnClickListener { hapticFeedback(); showScreen(R.id.productsScreen); loadProducts(); updateBottomNav(R.id.navProductsButton) }

        findViewById<View>(R.id.loginButton).setOnClickListener { hapticFeedback(); loginFromNativeForm() }
        findViewById<View>(R.id.loginConfigureServerButton).setOnClickListener { hapticFeedback(); showSettingsScreen() }

        findViewById<EditText>(R.id.loginPasswordInput).setOnEditorActionListener { _, _, _ ->
            loginFromNativeForm()
            true
        }

        findViewById<View>(R.id.homeAddProductButton).setOnClickListener { hapticFeedback(); clearProductForm(); showScreen(R.id.productFormScreen) }
        findViewById<View>(R.id.homeSearchButton).setOnClickListener {
            hapticFeedback()
            val query = findViewById<EditText>(R.id.homeSearchInput).text.toString().trim()
            val product = products.firstOrNull {
                query.isNotBlank() && (it.sku.equals(query, ignoreCase = true) || it.name.contains(query, ignoreCase = true))
            } ?: products.firstOrNull()
            if (product == null) showAppMessage("No hay productos para abrir ruta") else openProductRoute3d(product)
        }
        
        findViewById<EditText>(R.id.homeSearchInput).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = renderHomeResults()
            override fun afterTextChanged(s: Editable?) = Unit
        })

        findViewById<View>(R.id.addProductButton).setOnClickListener { hapticFeedback(); clearProductForm(); showScreen(R.id.productFormScreen) }
        findViewById<View>(R.id.saveProductButton).setOnClickListener { hapticFeedback(); saveProductFromForm() }
        
        findViewById<View>(R.id.homeScanButton).setOnClickListener { 
            hapticFeedback()
            scanOriginId = R.id.mobileDashboard
            openCameraForScanner() 
        }
        findViewById<View>(R.id.productsScanButton).setOnClickListener { 
            hapticFeedback()
            scanOriginId = R.id.productsScreen
            openCameraForScanner() 
        }

        
        findViewById<View>(R.id.openRoute3dButton).setOnClickListener { hapticFeedback(); openRoute3d() }
        findViewById<View>(R.id.closeRoute3dButton).setOnClickListener { hapticFeedback(); closeRoute3d() }
        findViewById<View>(R.id.shareReportButton).setOnClickListener { hapticFeedback(); shareReport() }
        findViewById<View>(R.id.scannerBackButton).setOnClickListener { hapticFeedback(); stopScanner(); showScreen(scanOriginId) }
        findViewById<View>(R.id.profileRefreshButton).setOnClickListener { hapticFeedback(); loadProfile() }
        findViewById<View>(R.id.profileSaveButton).setOnClickListener { hapticFeedback(); saveProfile() }
        findViewById<View>(R.id.profileChangePasswordButton).setOnClickListener { hapticFeedback(); changePasswordFromForm() }
        findViewById<View>(R.id.profileLogoutButton).setOnClickListener { hapticFeedback(); logoutFromMobile(false) }
        findViewById<View>(R.id.profileLogoutAllButton).setOnClickListener { hapticFeedback(); confirmLogoutAllDevices() }
        
        findViewById<EditText>(R.id.productsSearchInput).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentPageIndex = 0
                renderProducts()
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })
        
        findViewById<View>(R.id.filterAllButton).setOnClickListener { hapticFeedback(); productFilter = ProductFilter.ALL; currentPageIndex = 0; renderProducts() }
        findViewById<View>(R.id.filterUncategorizedButton).setOnClickListener { hapticFeedback(); productFilter = ProductFilter.UNCATEGORIZED; currentPageIndex = 0; renderProducts() }
        findViewById<View>(R.id.filterFirstShelfButton).setOnClickListener { hapticFeedback(); productFilter = ProductFilter.FIRST_SHELF; currentPageIndex = 0; renderProducts() }


        
        findViewById<View>(R.id.saveSettingsButton).setOnClickListener {
            hapticFeedback()
            val apiUrl = findViewById<EditText>(R.id.apiUrlInput).text.toString().trim()
            if (!apiUrl.startsWith("http://") && !apiUrl.startsWith("https://")) {
                showAppMessage("La URL debe empezar con http:// o https://")
                return@setOnClickListener
            }
            apiClient.setBaseUrl(apiUrl)
            preferences.edit {
                putString(PREF_API_BASE_URL, apiUrl)
                remove(PREF_SESSION_TOKEN)
            }
            showAppMessage("Configuración actualizada")
            showLoginScreen("Servidor actualizado. Inicia sesion nuevamente.")
        }
    }

    private fun restoreSessionOrShowLogin() {
        val savedApiUrl = preferences.getString(PREF_API_BASE_URL, null)
        if (!savedApiUrl.isNullOrBlank()) {
            apiClient.setBaseUrl(savedApiUrl)
            findViewById<EditText>(R.id.apiUrlInput).setText(savedApiUrl)
        } else if (isApiConfigured()) {
            // No saved URL but the build shipped a default: show it so it can be edited.
            findViewById<EditText>(R.id.apiUrlInput).setText(apiClient.currentBaseUrl())
        }
        val token = preferences.getString(PREF_SESSION_TOKEN, null)
        if (token.isNullOrBlank()) {
            if (isApiConfigured()) showLoginScreen() else showLoginScreen(getString(R.string.settings_not_configured))
            return
        }
        apiClient.setSessionToken(token)
        setLoginStatus("Validando sesion...", StatusKind.LOADING)
        executor.execute {
            repository.currentSession()
                .onSuccess { session ->
                    runOnUiThread {
                        preferences.edit { putString(PREF_SESSION_TOKEN, session.token) }
                        showAuthenticatedApp()
                    }
                }
                .onFailure {
                    runOnUiThread {
                        preferences.edit { remove(PREF_SESSION_TOKEN) }
                        apiClient.setSessionToken(null)
                        showLoginScreen("Tu sesion expiro. Inicia sesion nuevamente.")
                    }
                }
        }
    }

    private fun isApiConfigured(): Boolean = apiClient.currentBaseUrl().isNotBlank()

    private fun loginFromNativeForm() {
        if (!isApiConfigured()) {
            setLoginStatus(getString(R.string.settings_not_configured), StatusKind.WARNING)
            showSettingsScreen()
            return
        }
        val emailInput = findViewById<EditText>(R.id.loginEmailInput)
        val passwordInput = findViewById<EditText>(R.id.loginPasswordInput)
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()
        if (email.isBlank() || password.isBlank()) {
            setLoginStatus("Ingresa correo y contraseña.", StatusKind.WARNING)
            return
        }
        setLoginStatus("Iniciando sesion...", StatusKind.LOADING)
        executor.execute {
            repository.authenticate(email, password)
                .onSuccess { session ->
                    runOnUiThread {
                        preferences.edit {
                            putString(PREF_SESSION_TOKEN, session.token)
                            putString(PREF_LAST_EMAIL, session.user.email)
                        }
                        passwordInput.setText("")
                        showAppMessage("Sesion iniciada")
                        showAuthenticatedApp()
                    }
                }
                .onFailure { error ->
                    runOnUiThread {
                        setLoginStatus(error.message ?: "No se pudo iniciar sesion.", StatusKind.ERROR)
                    }
                }
        }
    }

    private fun showAuthenticatedApp() {
        showMobileDashboard()
        loadShelves()
        loadProducts()
        loadProfile()
    }

    private fun showLoginScreen(message: String = "Inicia sesion para continuar.") {
        hideAllScreens()
        findViewById<View>(R.id.bottomNavigation).isVisible = false
        routeWebView.stopLoading()
        loginScreen.isVisible = true
        val lastEmail = preferences.getString(PREF_LAST_EMAIL, "")
        if (!lastEmail.isNullOrBlank()) {
            findViewById<EditText>(R.id.loginEmailInput).setText(lastEmail)
        }
        setLoginStatus(message, StatusKind.INFO)
    }

    private fun setLoginStatus(message: String, kind: StatusKind) {
        findViewById<TextView>(R.id.loginStatusText).apply {
            text = message
            setTextColor(ContextCompat.getColor(this@MainActivity, when (kind) {
                StatusKind.ERROR -> R.color.error
                StatusKind.SUCCESS -> R.color.success
                StatusKind.WARNING -> R.color.warning
                StatusKind.INFO -> R.color.secondary
                else -> R.color.text_secondary
            }))
        }
    }

    private fun hapticFeedback() {
        window.decorView.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
    }

    private fun setProductsLoadingVisible(visible: Boolean) {
        findViewById<View>(R.id.homeLoadingIndicator).isVisible = visible
        findViewById<View>(R.id.productsLoadingIndicator).isVisible = visible
    }

    private fun showEmptyProductsState() {
        val message = getString(R.string.empty_catalog_message)
        setStatus(R.id.homeStatusText, message, StatusKind.EMPTY)
        setStatus(R.id.productsStatusText, message, StatusKind.EMPTY)
        findViewById<LinearLayout>(R.id.homeResultsList).apply {
            removeAllViews()
            addView(cardFactory.createCard(
                title = getString(R.string.no_products_registered_title),
                body = getString(R.string.no_products_registered_body),
                primaryActionText = getString(R.string.add_product),
                primaryAction = {
                    clearProductForm()
                    showScreen(R.id.productFormScreen)
                }
            ))
        }
        findViewById<LinearLayout>(R.id.productsList).apply {
            removeAllViews()
            addView(cardFactory.createCard(
                title = getString(R.string.empty_inventory_title),
                body = getString(R.string.empty_inventory_body),
                primaryActionText = getString(R.string.add_product),
                primaryAction = {
                    clearProductForm()
                    showScreen(R.id.productFormScreen)
                }
            ))
        }
    }

    private fun setProductsRetryVisible() {
        // No longer using these buttons in the redesigned UI
    }

    private fun showProductsConnectionError(error: Throwable?) {
        val detail = error?.message?.takeIf { it.isNotBlank() }?.let { " Detalle: $it" } ?: ""
        val message = getString(R.string.status_sync_error) + detail
        setStatus(R.id.homeStatusText, message, StatusKind.ERROR)
        setStatus(R.id.productsStatusText, message, StatusKind.ERROR)
        setProductsRetryVisible()
        findViewById<LinearLayout>(R.id.homeResultsList).removeAllViews()
        findViewById<LinearLayout>(R.id.productsList).removeAllViews()
        setProductsLoadingVisible(false)
    }

    private fun loadProducts() {
        setProductsRetryVisible()
        setProductsLoadingVisible(true)
        val loadingText = getString(R.string.loading_warehouse_products)
        setStatus(R.id.productsStatusText, loadingText, StatusKind.LOADING)
        
        findViewById<TextView>(R.id.homeStatusTextTitle)?.text = getString(R.string.synchronizing)
        findViewById<TextView>(R.id.homeStatusText)?.text = loadingText
        
        executor.execute {
            repository.fetchProducts()
                .onSuccess { loaded ->
                    products = loaded
                    cacheProducts(loaded)
                    runOnUiThread { 
                        setProductsLoadingVisible(false)
                        if (loaded.isEmpty()) showEmptyProductsState() 
                        else { 
                            renderProducts()
                            renderHomeResults()
                            findViewById<TextView>(R.id.homeStatusTextTitle)?.text = getString(R.string.products_available_format, loaded.size)
                            findViewById<TextView>(R.id.homeStatusText)?.text = getString(R.string.search_product_to_start_route)
                        }
                    }
                }
                .onFailure { error ->
                    products = loadCachedProducts()
                    runOnUiThread {
                        setProductsLoadingVisible(false)
                        if (products.isNotEmpty()) {
                            renderProducts()
                            setStatus(R.id.productsStatusText, getString(R.string.status_offline_cache), StatusKind.EMPTY)
                        } else {
                            showProductsConnectionError(error)
                        }
                    }
                }
        }
    }

    private fun renderProducts() {
        val query = findViewById<EditText>(R.id.productsSearchInput).text.toString().trim().lowercase()
        val firstShelfId = shelves.firstOrNull()?.id ?: products.firstOrNull()?.shelfId
        val visibleProducts = products
            .filter { query.isBlank() || it.sku.lowercase().contains(query) || it.name.lowercase().contains(query) || it.category.lowercase().contains(query) || it.brand.lowercase().contains(query) }
            .filter {
                when (productFilter) {
                    ProductFilter.ALL -> true
                    ProductFilter.UNCATEGORIZED -> it.category.contains("Sin categ", ignoreCase = true)
                    ProductFilter.FIRST_SHELF -> firstShelfId == null || it.shelfId == firstShelfId
                }
            }
        val list = findViewById<LinearLayout>(R.id.productsList)
        list.removeAllViews()

        if (products.isEmpty()) {
            showEmptyProductsState()
            return
        }

        if (visibleProducts.isEmpty()) {
            setStatus(R.id.productsStatusText, getString(R.string.status_no_matches), StatusKind.EMPTY)
            return
        }

        val pageSize = 20
        val totalPages = kotlin.math.ceil(visibleProducts.size.toDouble() / pageSize).toInt()
        currentPageIndex = currentPageIndex.coerceIn(0, (totalPages - 1).coerceAtLeast(0))
        
        val startIndex = currentPageIndex * pageSize
        val paginated = visibleProducts.drop(startIndex).take(pageSize)
        
        setStatus(R.id.productsStatusText, getString(R.string.pagination_format, currentPageIndex + 1, totalPages, visibleProducts.size), StatusKind.SUCCESS)

        
        paginated.forEach { product ->
            list.addView(
                cardFactory.createCard(
                    title = product.name,
                    body = "SKU: ${product.sku}\nCategoria: ${product.category}\nMarca: ${product.brand}\n${product.locationSummary(shelves)}",
                    primaryActionText = "VER RUTA 3D",
                    primaryAction = { openProductRoute3d(product) },
                    secondaryActionText = "EDITAR",
                    secondaryAction = { fillProductForm(product); showScreen(R.id.productFormScreen) }
                )
            )
        }

        // Navigation Controls
        if (totalPages > 1) {
            val navRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    topMargin = 24.dp()
                    bottomMargin = 32.dp()
                }
            }

            if (currentPageIndex > 0) {
                navRow.addView(Button(this).apply {
                    text = "← Anterior"
                    background = ContextCompat.getDrawable(this@MainActivity, R.drawable.white_button_background)
                    setTextColor(ContextCompat.getColor(this@MainActivity, R.color.warehouse_text))
                    setPadding(16.dp(), 0, 16.dp(), 0)
                    setOnClickListener { 
                        hapticFeedback()
                        currentPageIndex--
                        renderProducts()
                        findViewById<ScrollView>(R.id.productsScreen)?.fullScroll(View.FOCUS_UP)
                    }
                })
            }

            if (currentPageIndex < totalPages - 1) {
                navRow.addView(Button(this).apply {
                    text = "Siguiente →"
                    background = ContextCompat.getDrawable(this@MainActivity, R.drawable.blue_button_background)
                    setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                    setPadding(16.dp(), 0, 16.dp(), 0)
                    layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                        if (currentPageIndex > 0) marginStart = 16.dp()
                    }
                    setOnClickListener { 
                        hapticFeedback()
                        currentPageIndex++
                        renderProducts()
                        findViewById<ScrollView>(R.id.productsScreen)?.fullScroll(View.FOCUS_UP)
                    }
                })
            }

            list.addView(navRow)
        }
    }





    private fun renderHomeResults() {
        val query = findViewById<EditText>(R.id.homeSearchInput).text.toString().trim().lowercase()
        val list = findViewById<LinearLayout>(R.id.homeResultsList)
        list.removeAllViews()

        if (products.isEmpty()) {
            showEmptyProductsState()
            return
        }

        val visibleProducts = products
            .filter { query.isBlank() || it.sku.lowercase().contains(query) || it.name.lowercase().contains(query) || it.category.lowercase().contains(query) || it.brand.lowercase().contains(query) }
            .sortedWith(compareBy<Product> { if (query.isNotBlank() && it.sku.lowercase() == query) 0 else 1 }.thenBy { it.name.lowercase() })
            .take(6)

        if (visibleProducts.isEmpty()) {
            setStatus(R.id.homeStatusText, getString(R.string.home_search_not_found), StatusKind.EMPTY)
            return
        }

        val status = if (query.isBlank()) {
            getString(R.string.products_available_format, products.size) + " " + getString(R.string.search_product_to_start_route)
        } else {
            getString(R.string.home_search_found_format, visibleProducts.size)
        }
        setStatus(R.id.homeStatusText, status, StatusKind.SUCCESS)

        visibleProducts.forEach { product ->
            val meta = listOf(
                "SKU ${product.sku}",
                product.category,
                product.brand,
                product.locationSummary(shelves)
            ).filter { it.isNotBlank() }.joinToString(" · ")
            list.addView(
                cardFactory.createSuggestionRow(
                    primary = product.name,
                    secondary = meta,
                    query = query,
                    onClick = { openProductRoute3d(product) }
                )
            )
        }
    }

    private fun openProductRoute3d(product: Product) {
        selectedRouteProduct = product
        showAppMessage("Ruta 3D hacia ${product.name}")
        openRoute3d()
    }
    
    private fun buildRouteSteps(product: Product, shelf: Shelf?): List<String> {
        val shelfName = shelf?.let { "${it.id} · ${it.label}" } ?: "Estante ${product.shelfId}"
        return listOf(
            "Dirígete al pasillo principal y localiza el $shelfName.",
            "Ubícate frente al estante en la ${product.sectionLabel(shelf)}.",
            "El producto se encuentra en el ${product.levelLabel(shelf)}.",
            "Verifica el SKU ${product.sku} antes de proceder."
        )
    }

    private fun loadShelves() {
        setStatus(R.id.shelvesStatusText, getString(R.string.shelves_syncing), StatusKind.LOADING)
        executor.execute {
            repository.fetchShelves()
                .onSuccess { loaded ->
                    shelves = loaded
                    cacheShelves(loaded)
                    runOnUiThread { renderShelves() }
                }
                .onFailure {
                    shelves = loadCachedShelves()
                    runOnUiThread {
                        if (shelves.isNotEmpty()) {
                            renderShelves()
                            setStatus(R.id.shelvesStatusText, getString(R.string.status_offline_shelves), StatusKind.EMPTY)
                        } else {
                            setStatus(R.id.shelvesStatusText, getString(R.string.status_sync_error), StatusKind.ERROR)
                        }
                    }
                }
        }
    }

    private fun renderShelves() {
        val list = findViewById<LinearLayout>(R.id.shelvesList)
        list.removeAllViews()

        if (shelves.isEmpty()) {
            setStatus(R.id.shelvesStatusText, getString(R.string.no_shelves_registered), StatusKind.EMPTY)
            return
        }

        setStatus(R.id.shelvesStatusText, getString(R.string.shelves_count_format, shelves.size), StatusKind.SUCCESS)
        shelves.forEach { shelf ->
            list.addView(
                cardFactory.createCard(
                    title = shelf.label,
                    body = "ID: ${shelf.id}\n${shelf.sections} secciones · ${shelf.width.cleanText()}x${shelf.height.cleanText()}x${shelf.depth.cleanText()} cm"
                )
            )
        }
    }

    private fun loadProfile() {
        setStatus(R.id.profileStatusText, getString(R.string.profile_syncing), StatusKind.LOADING)
        executor.execute {
            val sessionResult = repository.currentSession()
            val sessionsResult = repository.fetchActiveSessions()
            runOnUiThread {
                sessionResult
                    .onSuccess { session ->
                        currentUserRole = session.user.role
                        findViewById<TextView>(R.id.profileAvatarText).text = avatarInitialsFor(session.user.name)
                        findViewById<TextView>(R.id.profileNameText).text = session.user.name
                        findViewById<TextView>(R.id.profileEmailText).text = session.user.email
                        findViewById<TextView>(R.id.profileRoleText).text = session.user.role.ifBlank { "consulta" }
                        findViewById<TextView>(R.id.profileExpiresText).text =
                            "Sesion activa hasta: ${formatSessionTimestamp(session.expiresAt)}"
                        findViewById<EditText>(R.id.profileNameInput).setText(session.user.name)
                        findViewById<EditText>(R.id.profileEmailInput).setText(session.user.email)
                        setStatus(R.id.profileStatusText, getString(R.string.profile_synced), StatusKind.SUCCESS)
                    }
                    .onFailure { error ->
                        setStatus(R.id.profileStatusText, error.message ?: getString(R.string.profile_sync_error), StatusKind.ERROR)
                    }

                sessionsResult
                    .onSuccess { renderActiveSessions(it) }
                    .onFailure { renderActiveSessions(emptyList()) }
            }
        }
    }

    private fun renderActiveSessions(sessions: List<ActiveSession>) {
        val list = findViewById<LinearLayout>(R.id.profileSessionsList)
        list.removeAllViews()
        if (sessions.isEmpty()) {
            list.addView(cardFactory.createCard(getString(R.string.active_sessions_title), getString(R.string.active_sessions_error)))
            return
        }
        sessions.forEach { session ->
            val label = if (session.current) "Este dispositivo · activo" else "Otro dispositivo"
            val device = describeUserAgent(session.userAgent)
            list.addView(
                cardFactory.createCard(
                    label,
                    "${device} · ${session.ipAddress}\n" +
                        "Ultimo uso: ${formatSessionTimestamp(session.lastSeenAt)}\n" +
                        "Expira: ${formatSessionTimestamp(session.expiresAt)}"
                )
            )
        }
    }

    private fun saveProfile() {
        val name = findViewById<EditText>(R.id.profileNameInput).text.toString().trim()
        val email = findViewById<EditText>(R.id.profileEmailInput).text.toString().trim()
        if (name.isBlank() || email.isBlank()) {
            showAppMessage("Nombre y correo son obligatorios")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setStatus(R.id.profileStatusText, "El correo no tiene un formato valido.", StatusKind.WARNING)
            return
        }
        // If the email changed, the API requires the current password. Read it from the
        // Seguridad section so the user only needs to fill one place.
        val currentPassword = findViewById<EditText>(R.id.profileCurrentPasswordInput).text.toString()
        setStatus(R.id.profileStatusText, "Guardando perfil...", StatusKind.LOADING)
        executor.execute {
            repository.updateProfile(name, email, currentPassword, "")
                .onSuccess {
                    runOnUiThread {
                        showAppMessage("Perfil actualizado")
                        findViewById<EditText>(R.id.profileCurrentPasswordInput).setText("")
                        loadProfile()
                    }
                }
                .onFailure { error ->
                    runOnUiThread {
                        setStatus(R.id.profileStatusText, error.message ?: "No se pudo guardar el perfil", StatusKind.ERROR)
                    }
                }
        }
    }

    private fun changePasswordFromForm() {
        val currentPassword = findViewById<EditText>(R.id.profileCurrentPasswordInput).text.toString()
        val newPassword = findViewById<EditText>(R.id.profileNewPasswordInput).text.toString()
        val confirmPassword = findViewById<EditText>(R.id.profileConfirmPasswordInput).text.toString()
        if (currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            setStatus(R.id.profileStatusText, "Completa los tres campos de contrasena.", StatusKind.WARNING)
            return
        }
        if (newPassword.length < 6) {
            setStatus(R.id.profileStatusText, "La nueva contrasena debe tener al menos 6 caracteres.", StatusKind.WARNING)
            return
        }
        if (newPassword != confirmPassword) {
            setStatus(R.id.profileStatusText, "La nueva contrasena y su confirmacion no coinciden.", StatusKind.WARNING)
            return
        }
        // Send the existing name/email so the API receives a complete payload.
        val name = findViewById<EditText>(R.id.profileNameInput).text.toString().trim()
        val email = findViewById<EditText>(R.id.profileEmailInput).text.toString().trim()
        setStatus(R.id.profileStatusText, "Cambiando contrasena...", StatusKind.LOADING)
        executor.execute {
            repository.updateProfile(name, email, currentPassword, newPassword)
                .onSuccess {
                    runOnUiThread {
                        showAppMessage("Contrasena actualizada")
                        findViewById<EditText>(R.id.profileCurrentPasswordInput).setText("")
                        findViewById<EditText>(R.id.profileNewPasswordInput).setText("")
                        findViewById<EditText>(R.id.profileConfirmPasswordInput).setText("")
                        loadProfile()
                    }
                }
                .onFailure { error ->
                    runOnUiThread {
                        setStatus(R.id.profileStatusText, error.message ?: "No se pudo cambiar la contrasena.", StatusKind.ERROR)
                    }
                }
        }
    }

    private fun confirmLogoutAllDevices() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Cerrar sesion en todos los dispositivos")
            .setMessage("Se cerraran todas las sesiones de tu cuenta. Tendras que iniciar sesion otra vez en cada dispositivo.")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Cerrar todas") { _, _ -> logoutFromMobile(true) }
            .show()
    }

    private fun avatarInitialsFor(name: String): String {
        val parts = name.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        return when {
            parts.isEmpty() -> "U"
            parts.size == 1 -> parts[0].first().uppercaseChar().toString()
            else -> "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
        }
    }

    private fun describeUserAgent(userAgent: String): String {
        if (userAgent.isBlank() || userAgent.equals("Dispositivo", ignoreCase = true)) return "Dispositivo"
        val os = when {
            userAgent.contains("Android", ignoreCase = true) -> "Android"
            userAgent.contains("iPhone", ignoreCase = true) || userAgent.contains("iPad", ignoreCase = true) -> "iOS"
            userAgent.contains("Windows", ignoreCase = true) -> "Windows"
            userAgent.contains("Mac OS", ignoreCase = true) || userAgent.contains("Macintosh", ignoreCase = true) -> "macOS"
            userAgent.contains("Linux", ignoreCase = true) -> "Linux"
            else -> "Dispositivo"
        }
        val browser = when {
            userAgent.contains("Edg/", ignoreCase = true) -> "Edge"
            userAgent.contains("Chrome", ignoreCase = true) -> "Chrome"
            userAgent.contains("Firefox", ignoreCase = true) -> "Firefox"
            userAgent.contains("Safari", ignoreCase = true) -> "Safari"
            userAgent.startsWith("almacen", ignoreCase = true) -> "App nativa"
            else -> ""
        }
        return if (browser.isBlank()) os else "$browser en $os"
    }

    private fun formatSessionTimestamp(raw: String): String {
        if (raw.isBlank() || raw == "-") return "-"
        val instant = parseServerInstant(raw) ?: return raw
        val diffMs = instant.toEpochMilli() - System.currentTimeMillis()
        val absMs = kotlin.math.abs(diffMs)
        val minutes = absMs / 60_000
        val hours = minutes / 60
        val days = hours / 24
        val relative = when {
            minutes < 1L -> if (diffMs >= 0) "en instantes" else "hace instantes"
            minutes < 60L -> if (diffMs >= 0) "en $minutes min" else "hace $minutes min"
            hours < 24L -> if (diffMs >= 0) "en $hours h" else "hace $hours h"
            days < 30L -> if (diffMs >= 0) "en $days d" else "hace $days d"
            else -> null
        }
        val absolute = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
            .format(java.util.Date(instant.toEpochMilli()))
        return if (relative != null) "$absolute ($relative)" else absolute
    }

    private fun parseServerInstant(raw: String): java.time.Instant? {
        // Server returns timestamps like "2026-05-26 13:32:41" (MySQL DATETIME) or ISO-8601.
        return runCatching { java.time.Instant.parse(raw) }
            .recoverCatching {
                val normalized = raw.trim().replace(' ', 'T')
                java.time.LocalDateTime
                    .parse(normalized)
                    .atZone(java.time.ZoneId.systemDefault())
                    .toInstant()
            }
            .getOrNull()
    }

    private fun logoutFromMobile(allDevices: Boolean) {
        executor.execute {
            repository.logout(allDevices)
            runOnUiThread {
                showAppMessage(if (allDevices) "Sesiones cerradas" else "Sesion cerrada")
                preferences.edit { remove(PREF_SESSION_TOKEN) }
                apiClient.setSessionToken(null)
                products = emptyList()
                shelves = emptyList()
                showLoginScreen("Sesion cerrada correctamente.")
            }
        }
    }

    private fun saveProductFromForm() {
        val product = readProductForm() ?: return
        setStatus(R.id.productsStatusText, "Sincronizando con servidor...", StatusKind.LOADING)
        executor.execute {
            repository.saveProduct(product)
                .onSuccess {
                    runOnUiThread {
                        showScreen(R.id.productsScreen)
                        showAppMessage("Guardado exitosamente")
                        showNativeNotification("Catálogo Actualizado", "Producto ${product.sku} guardado.")
                        loadProducts()
                    }
                }
                .onFailure { error ->
                    runOnUiThread {
                        MaterialAlertDialogBuilder(this)
                            .setTitle("Error de Sincronización")
                            .setMessage(error.message ?: "Servidor no disponible")
                            .setPositiveButton("REINTENTAR", null)
                            .show()
                    }
                }
        }
    }

    private fun readProductForm(): Product? {
        val sku = findViewById<EditText>(R.id.productSkuInput).text.toString().trim()
        val name = findViewById<EditText>(R.id.productNameInput).text.toString().trim()
        val category = findViewById<EditText>(R.id.productCategoryInput).text.toString().trim().ifBlank { "Sin categoria" }
        val brand = findViewById<EditText>(R.id.productBrandInput).text.toString().trim().ifBlank { "Sin marca" }
        val shelfId = findViewById<EditText>(R.id.productShelfInput).text.toString().trim()
        val w = findViewById<EditText>(R.id.productWidthInput).text.toString().toDoubleOrNull()
        val h = findViewById<EditText>(R.id.productHeightInput).text.toString().toDoubleOrNull()
        val d = findViewById<EditText>(R.id.productDepthInput).text.toString().toDoubleOrNull()

        if (sku.isBlank() || name.isBlank() || shelfId.isBlank() || w == null || h == null || d == null) {
            showAppMessage("Completa SKU, nombre, estante y dimensiones")
            return null
        }

        return Product(sku, name, category, brand, shelfId, w, h, d)
    }

    private fun clearProductForm() {
        editingProductSku = null
        findViewById<EditText>(R.id.productSkuInput).setText("")
        findViewById<EditText>(R.id.productNameInput).setText("")
        findViewById<EditText>(R.id.productCategoryInput).setText("")
        findViewById<EditText>(R.id.productBrandInput).setText("")
        findViewById<EditText>(R.id.productShelfInput).setText(shelves.firstOrNull()?.id ?: "S01")
        findViewById<EditText>(R.id.productWidthInput).setText("")
        findViewById<EditText>(R.id.productHeightInput).setText("")
        findViewById<EditText>(R.id.productDepthInput).setText("")
    }

    private fun fillProductForm(product: Product) {
        editingProductSku = product.sku
        findViewById<EditText>(R.id.productSkuInput).setText(product.sku)
        findViewById<EditText>(R.id.productNameInput).setText(product.name)
        findViewById<EditText>(R.id.productCategoryInput).setText(product.category)
        findViewById<EditText>(R.id.productBrandInput).setText(product.brand)
        findViewById<EditText>(R.id.productShelfInput).setText(product.shelfId)
        findViewById<EditText>(R.id.productWidthInput).setText(product.width.cleanText())
        findViewById<EditText>(R.id.productHeightInput).setText(product.height.cleanText())
        findViewById<EditText>(R.id.productDepthInput).setText(product.depth.cleanText())
    }

    private fun setStatus(viewId: Int, message: String, kind: StatusKind) {
        findViewById<TextView>(viewId).apply {
            text = message
	            setTextColor(ContextCompat.getColor(this@MainActivity, when(kind) {
	                StatusKind.ERROR -> R.color.error
	                StatusKind.SUCCESS -> R.color.success
	                StatusKind.WARNING -> R.color.warning
	                StatusKind.INFO -> R.color.secondary
	                else -> R.color.text_secondary
	            }))
	        }
    }

    private fun showAppMessage(message: String) {
        hapticFeedback()
        Snackbar.make(findViewById(R.id.main), message, Snackbar.LENGTH_SHORT).show()
    }

    private fun openCameraForScanner() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), NATIVE_CAMERA_REQUEST)
            return
        }
        startScanner()
    }

    private fun startScanner() {
        hideAllScreens()
        findViewById<View>(R.id.bottomNavigation).isVisible = false
        scannerScreen.isVisible = true

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()
        barcodeScanner = BarcodeScanning.getClient(options)

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(scannerPreview.surfaceProvider)
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                    barcodeScanner?.process(image)
                        ?.addOnSuccessListener { barcodes ->
                            for (barcode in barcodes) {
                                val rawValue = barcode.rawValue
                                if (rawValue != null) {
                                    runOnUiThread {
                                        onBarcodeScanned(rawValue)
                                    }
                                    break
                                }
                            }
                        }
                        ?.addOnCompleteListener {
                            imageProxy.close()
                        }
                } else {
                    imageProxy.close()
                }
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis)
            } catch (e: Exception) {
                showAppMessage("Error al iniciar cámara: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun onBarcodeScanned(sku: String) {
        stopScanner()
        if (scanOriginId == R.id.mobileDashboard) {
            findViewById<EditText>(R.id.homeSearchInput).setText(sku)
            showMobileDashboard()
            renderHomeResults()
        } else {
            findViewById<EditText>(R.id.productsSearchInput).setText(sku)
            showScreen(R.id.productsScreen)
            renderProducts()
        }
        showAppMessage("Buscando código: $sku")
    }

    private fun stopScanner() {
        ProcessCameraProvider.getInstance(this).get().unbindAll()
        barcodeScanner?.close()
        barcodeScanner = null
    }

    private fun shareReport() {
        val report = "Reporte Almacén 3D\nProductos: ${products.size}\nEstantes: ${shelves.size}"
        startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(Intent.EXTRA_TEXT, report) }, "Compartir"))
    }

    private fun setupRouteWebView() {
        routeWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        routeWebView.settings.apply {
            javaScriptEnabled = true; domStorageEnabled = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            cacheMode = WebSettings.LOAD_DEFAULT
            mediaPlaybackRequiresUserGesture = true
            loadsImagesAutomatically = true
            blockNetworkImage = false
        }
        val assetLoader = WebViewAssetLoader.Builder().addPathHandler("/local_assets/", WebViewAssetLoader.AssetsPathHandler(this)).build()
        routeWebView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(v: WebView, r: WebResourceRequest): WebResourceResponse? = assetLoader.shouldInterceptRequest(r.url)
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                return request.url.host != "appassets.androidplatform.net"
            }
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                injectNativeSessionIntoRouteViewer(view, url)
            }
        }
    }

    private fun openRoute3d() {
        val p = selectedRouteProduct ?: return
        if (!apiClient.hasSession()) {
            showLoginScreen("Inicia sesion para ver la ruta 3D.")
            return
        }
        val token = apiClient.currentToken()
        if (token.isNullOrBlank()) {
            showLoginScreen("Tu sesion expiro. Inicia sesion para ver la ruta 3D.")
            return
        }
        hideAllScreens(); findViewById<View>(R.id.bottomNavigation).isVisible = false
        route3dContainer.isVisible = true
        routeTokenInjectedForUrl = null
        val sku = URLEncoder.encode(p.sku, "UTF-8")
        val encodedToken = URLEncoder.encode(token, "UTF-8")
        val apiBase = URLEncoder.encode(apiClient.currentBaseUrl(), "UTF-8")
        
        // Use the native base URL from the API client as the source of truth
        val actualApiBase = apiClient.currentBaseUrl()
        val url = "https://appassets.androidplatform.net/local_assets/index.html?mode=mobile-route&sku=$sku&nativeToken=$encodedToken&nativeApiBase=$apiBase&apiBase=${URLEncoder.encode(actualApiBase, "UTF-8")}"
        
        routeWebView.loadUrl(url)
        routeWebView.onResume()
    }

    private fun closeRoute3d() {
        routeWebView.stopLoading()
        routeWebView.onPause()
        routeWebView.loadUrl("about:blank")
        routeTokenInjectedForUrl = null
        showMobileDashboard()
    }

    private fun injectNativeSessionIntoRouteViewer(view: WebView, url: String) {
        if (!url.contains("mode=mobile-route")) return
        val token = apiClient.currentToken()
        if (token.isNullOrBlank()) {
            showLoginScreen("Tu sesion expiro. Inicia sesion para ver la ruta 3D.")
            return
        }
        if (routeTokenInjectedForUrl == url) return
        routeTokenInjectedForUrl = url
        val quotedToken = JSONObject.quote(token)
        val quotedApiBase = JSONObject.quote(apiClient.currentBaseUrl())
        view.evaluateJavascript(
            """
            (function() {
              localStorage.setItem('almacen-digital-session-token', $quotedToken);
              localStorage.setItem('almacen-digital-api-base', $quotedApiBase);
              if (!window.__almacenNativeRouteSessionReady) {
                window.__almacenNativeRouteSessionReady = true;
                location.reload();
              }
            })();
            """.trimIndent(),
            null
        )
    }

    private fun showMobileDashboard() {
        if (!apiClient.hasSession()) {
            showLoginScreen()
            return
        }
        hideAllScreens()
        findViewById<View>(R.id.bottomNavigation).isVisible = true
        mobileDashboard.isVisible = true
    }

    private fun showScreen(id: Int) {
        if (!apiClient.hasSession()) {
            showLoginScreen()
            return
        }
        hideAllScreens()
        findViewById<View>(R.id.bottomNavigation).isVisible = true
        findViewById<View>(id).isVisible = true
    }
    private fun hideAllScreens() { appScreens.forEach { it.isVisible = false } }

    // Settings is reachable before login so the server URL can be configured on first launch.
    private fun showSettingsScreen() {
        hideAllScreens()
        findViewById<View>(R.id.bottomNavigation).isVisible = apiClient.hasSession()
        findViewById<View>(R.id.settingsScreen).isVisible = true
    }

    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (scannerScreen.isVisible) {
                    stopScanner()
                    showScreen(scanOriginId)
                }
                else if (route3dContainer.isVisible) closeRoute3d()
                else if (loginScreen.isVisible) showExitDialog()
                else if (!mobileDashboard.isVisible) showMobileDashboard()
                else showExitDialog()
            }
        })
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val s = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(s.left, s.top, s.right, s.bottom); insets
        }
    }

    private fun showExitDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Finalizar Sesión")
            .setMessage("¿Estás seguro de que deseas salir de Almacén 3D?")
            .setPositiveButton("SALIR") { _, _ -> finish() }
            .setNegativeButton("CANCELAR", null)
            .show()
    }

    private fun cacheProducts(v: List<Product>) {
        val json = JSONArray()
        v.forEach { p ->
            json.put(JSONObject().apply {
                put("sku", p.sku); put("name", p.name); put("category", p.category); put("brand", p.brand); put("shelfId", p.shelfId)
                put("width", p.width); put("height", p.height); put("depth", p.depth)
                put("localX", p.localX); put("localY", p.localY); put("localZ", p.localZ)
            })
        }
        preferences.edit { putString("cache_products", json.toString()) }
    }
    
    private fun loadCachedProducts(): List<Product> {
        val raw = preferences.getString("cache_products", null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            List(arr.length()) { i ->
                val o = arr.getJSONObject(i)
                Product(
                    o.getString("sku"),
                    o.optString("name", o.getString("sku")),
                    o.optString("category", "Sin categoria"),
                    o.optString("brand", "Sin marca"),
                    o.getString("shelfId"),
                    o.getDouble("width"),
                    o.getDouble("height"),
                    o.getDouble("depth"),
                    localX = o.optDouble("localX", 0.0),
                    localY = o.optDouble("localY", 0.0),
                    localZ = o.optDouble("localZ", 0.0)
                )
            }
        } catch (e: Exception) { emptyList() }
    }
    
    private fun cacheShelves(v: List<Shelf>) {
        val json = JSONArray()
        v.forEach { s ->
            json.put(JSONObject().apply {
                put("id", s.id); put("label", s.label); put("sections", s.sections)
                put("width", s.width); put("height", s.height); put("depth", s.depth); put("rotationY", s.rotationY)
            })
        }
        preferences.edit { putString("cache_shelves", json.toString()) }
    }
    
    private fun loadCachedShelves(): List<Shelf> {
        val raw = preferences.getString("cache_shelves", null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            List(arr.length()) { i ->
                val o = arr.getJSONObject(i)
                Shelf(
                    o.getString("id"), 
                    o.getString("label"), 
                    o.getInt("sections"), 
                    o.getDouble("width"), 
                    o.getDouble("height"), 
                    o.getDouble("depth"),
                    o.optDouble("rotationY", 0.0)
                )
            }
        } catch (e: Exception) { emptyList() }
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("updates", "Actualizaciones", NotificationManager.IMPORTANCE_DEFAULT)
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }
    }
    
    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1003)
            }
        }
    }
    
    private fun showNativeNotification(title: String, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && 
            ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val builder = NotificationCompat.Builder(this, "updates")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), builder.build())
    }

    override fun onPause() {
        if (this::routeWebView.isInitialized) {
            routeWebView.onPause()
        }
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (this::routeWebView.isInitialized && this::route3dContainer.isInitialized && route3dContainer.isVisible) {
            routeWebView.onResume()
        }
    }

    override fun onDestroy() { 
        executor.shutdownNow()
        cameraExecutor.shutdownNow()
        routeWebView.destroy()
        super.onDestroy() 
    }

    companion object {
        private const val NATIVE_CAMERA_REQUEST = 1002
        private const val PREF_SESSION_TOKEN = "session_token"
        private const val PREF_LAST_EMAIL = "last_email"
        private const val PREF_API_BASE_URL = "api_base_url"
    }
}

private enum class ProductFilter { ALL, UNCATEGORIZED, FIRST_SHELF }

private fun Product.locationSummary(shelves: List<Shelf>): String {
    val s = shelves.firstOrNull { it.id == shelfId }
    return "${s?.label ?: "Estante $shelfId"} · ${sectionLabel(s)} · ${levelLabel(s)}"
}

private fun Product.sectionLabel(s: Shelf?): String {
    val sec = s?.sections?.coerceAtLeast(1) ?: 1
    if (sec == 1) return "Sec. única"
    val w = s?.width?.takeIf { it > 0.0 } ?: return "Sec. 1"
    return "Sección ${((localX + w/2)/w * sec).toInt() + 1}"
}

private fun Product.levelLabel(s: Shelf?): String {
    val h = s?.height?.takeIf { it > 0.0 } ?: return "Nivel bajo"
    val y = (localY/h).coerceIn(0.0, 1.0)
    return when { y < 0.34 -> "Nivel bajo"; y < 0.67 -> "Nivel medio"; else -> "Nivel alto" }
}
