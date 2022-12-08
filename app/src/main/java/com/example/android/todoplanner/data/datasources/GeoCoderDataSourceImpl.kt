package com.example.android.todoplanner.data.datasources

import android.content.Context
import android.location.Geocoder
import com.example.android.todoplanner.ErrorCode
import com.example.android.todoplanner.data.model.DataLatLon
import java.lang.Exception
import javax.inject.Inject

class GeoCoderDataSourceImpl @Inject constructor (
    private val context: Context
) : GeoCoderDataSource {

    override suspend fun getLatLon(location: String): DataLatLon {

        var result: DataLatLon? = null

        if (Geocoder.isPresent()) {
            var lat: Double
            var lon: Double
            val geoCoder = Geocoder(context)

            try {
                @Suppress("DEPRECATION")
                val addresses = geoCoder.getFromLocationName(location, 1)
                addresses?.let {
                    val address = it[0]
                    lat = address.latitude
                    lon = address.longitude
                    result = DataLatLon.LatLon(lat, lon)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return result ?: DataLatLon.Error(ErrorCode.SERVICE_UNAVAILABLE)
    }
}

// связь между двумя корутинами можно реализовать через channels
//if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//    // версия API 33 и выше
//    geoCoder.getFromLocationName(location, 1) { list ->
//        // вызывается из MainThread
//        if (list.isNotEmpty()) {
//            val address = list[0]
//            address?.let {
//                запустить корутину из которой получать данные в основную функцию через канал (с таймаутом)
//            }
//        }
//    }
//}
// при нажатии кнопки Back и последующем уничтожении фрагмента
// связанная с ним через ViewModelStoreOwner ViewModel уничтожается
// при уничтожении ViewModel во viewModelScope-корутинах возникает исключение CancellationException
// однако, далее это исключение не распространяется наверх и не приводит к срабатыванию CoroutineExceptionHandler
// эта ситуация является исключением из общей логике обработки исключений в корутинах
// CancellationException можно обработать внутри корутины, чтобы освободить ресурсы и при необходимости
// сгенерировать новое исключение, которое уже обработать в CoroutineExceptionHandler
// См. документацию:
// Cancellation is closely related to exceptions. Coroutines internally use CancellationException
// for cancellation, these exceptions are ignored by all handlers, so they should be used only
// as the source of additional debug information, which can be obtained by catch block.
// When a coroutine is cancelled using Job.cancel, it terminates, but it does not cancel its parent.
