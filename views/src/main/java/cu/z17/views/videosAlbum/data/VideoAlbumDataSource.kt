package cu.z17.views.videosAlbum.data

import androidx.paging.PagingSource
import androidx.paging.PagingState

class VideoAlbumDataSource(private val onFetch: suspend (limit: Int, offset: Int) -> List<VideoAlbum>) :
    PagingSource<Int, VideoAlbum>() {

    override fun getRefreshKey(state: PagingState<Int, VideoAlbum>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(One)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(One)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VideoAlbum> {
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