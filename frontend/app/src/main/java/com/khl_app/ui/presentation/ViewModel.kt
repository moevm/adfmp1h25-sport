import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BaseViewModel : ViewModel() {
    protected val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    protected val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    protected suspend fun handleError(e: Exception, message: String) {
        val errorMsg = "$message: ${e.message}"
        _error.value = errorMsg
    }
}