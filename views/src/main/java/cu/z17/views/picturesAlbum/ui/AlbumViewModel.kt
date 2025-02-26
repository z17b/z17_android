package cu.z17.views.picturesAlbum.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cu.z17.views.picturesAlbum.data.AlbumImage
import cu.z17.views.picturesAlbum.data.ImageRepository
import cu.z17.views.utils.Z17MutableListFlow
import kotlinx.coroutines.flow.Flow

class AlbumViewModel(
    private val imageRepository: ImageRepository,
) : ViewModel() {

    val selectedList = Z17MutableListFlow<AlbumImage>(emptyList())

    fun getImages(): Flow<PagingData<AlbumImage>> = Pager(
        config = PagingConfig(
            pageSize = 10,// Especifica el tamaño de cada página de datos que se cargará. Por ejemplo, si se establece en 20, cada vez que se solicite una página, se cargarán 20 elementos. Este parámetro es obligatorio.
            prefetchDistance = 10,// Indica el número de elementos que se deben precargar antes de llegar al final de la lista actualmente visible. Esto permite cargar de manera anticipada los datos para una experiencia de desplazamiento suave. El valor predeterminado es el tamaño de la página.
            initialLoadSize = 20,// Especifica el número de elementos que se deben cargar inicialmente. Esto determina el tamaño de la primera página de datos que se carga cuando se crea la lista. El valor predeterminado es el tamaño de la página.
            maxSize = PagingConfig.MAX_SIZE_UNBOUNDED,// Define el tamaño máximo de la lista paginada. Puede ser un número finito o PagingConfig.MAX_SIZE_UNBOUNDED para indicar que no hay límite máximo. Si se alcanza el tamaño máximo, no se cargarán más elementos. El valor predeterminado es PagingConfig.MAX_SIZE_UNBOUNDED.
            enablePlaceholders = true,
        )
    ) {
        imageRepository.getPicturePagingSource()
    }.flow.cachedIn(viewModelScope)
}
