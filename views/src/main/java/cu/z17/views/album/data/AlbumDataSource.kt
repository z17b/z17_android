package cu.z17.views.album.data

import androidx.paging.PagingSource
import androidx.paging.PagingState

private const val Zero = 0
private const val One = 1

class AlbumDataSource(private val onFetch: (limit: Int, offset: Int) -> List<AlbumImage>) :
    PagingSource<Int, AlbumImage>() {

    override fun getRefreshKey(state: PagingState<Int, AlbumImage>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(One)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(One)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AlbumImage> {
        val pageNumber = params.key ?: Zero
        val pageSize = params.loadSize
        val pictures = onFetch.invoke(pageSize, pageNumber * pageSize)
        val prevKey = if (pageNumber > Zero) pageNumber.minus(One) else null
        val nextKey = if (pictures.isNotEmpty()) pageNumber.plus(One) else null

        return LoadResult.Page(
            data = pictures,
            prevKey = prevKey,
            nextKey = nextKey
        )
    }
}
