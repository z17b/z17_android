package cu.z17.views.imageEditor2.sections.cropper.components.settings

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.ImageBitmap
import cu.z17.views.imageEditor2.sections.cropper.components.model.CropFrame
import cu.z17.views.imageEditor2.sections.cropper.components.model.CropOutline
import cu.z17.views.imageEditor2.sections.cropper.components.model.CropOutlineContainer
import cu.z17.views.imageEditor2.sections.cropper.components.model.CustomOutlineContainer
import cu.z17.views.imageEditor2.sections.cropper.components.model.CustomPathOutline
import cu.z17.views.imageEditor2.sections.cropper.components.model.CutCornerCropShape
import cu.z17.views.imageEditor2.sections.cropper.components.model.CutCornerRectOutlineContainer
import cu.z17.views.imageEditor2.sections.cropper.components.model.ImageMaskOutline
import cu.z17.views.imageEditor2.sections.cropper.components.model.ImageMaskOutlineContainer
import cu.z17.views.imageEditor2.sections.cropper.components.model.OutlineType
import cu.z17.views.imageEditor2.sections.cropper.components.model.OvalCropShape
import cu.z17.views.imageEditor2.sections.cropper.components.model.OvalOutlineContainer
import cu.z17.views.imageEditor2.sections.cropper.components.model.PolygonCropShape
import cu.z17.views.imageEditor2.sections.cropper.components.model.PolygonOutlineContainer
import cu.z17.views.imageEditor2.sections.cropper.components.model.PolygonProperties
import cu.z17.views.imageEditor2.sections.cropper.components.model.RectCropShape
import cu.z17.views.imageEditor2.sections.cropper.components.model.RectOutlineContainer
import cu.z17.views.imageEditor2.sections.cropper.components.model.RoundedCornerCropShape
import cu.z17.views.imageEditor2.sections.cropper.components.model.RoundedRectOutlineContainer
import cu.z17.views.imageEditor2.sections.cropper.components.util.createPolygonShape

class CropFrameFactory(private val defaultImages: List<ImageBitmap>) {

    private val cropFrames = mutableStateListOf<CropFrame>()

    fun getCropFrames(): List<CropFrame> {
        if (cropFrames.isEmpty()) {
            val temp = mutableListOf<CropFrame>()
            OutlineType.values().forEach {
                temp.add(getCropFrame(it))
            }
            cropFrames.addAll(temp)
        }
        return cropFrames
    }

    fun getCropFrame(outlineType: OutlineType): CropFrame {
        return cropFrames
            .firstOrNull { it.outlineType == outlineType } ?: createDefaultFrame(outlineType)
    }

    private fun createDefaultFrame(outlineType: OutlineType): CropFrame {
        return when (outlineType) {
            OutlineType.Rect -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = false,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }

            OutlineType.RoundedRect -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = true,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }

            OutlineType.CutCorner -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = true,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }

            OutlineType.Oval -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = true,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }

            OutlineType.Polygon -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = true,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }


            OutlineType.Custom -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = true,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }

            OutlineType.ImageMask -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = true,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }
        }
    }

    private fun createCropOutlineContainer(
        outlineType: OutlineType
    ): CropOutlineContainer<out CropOutline> {
        return when (outlineType) {
            OutlineType.Rect -> {
                RectOutlineContainer(
                    outlines = listOf(RectCropShape(id = 0, title = "Rect"))
                )
            }

            OutlineType.RoundedRect -> {
                RoundedRectOutlineContainer(
                    outlines = listOf(RoundedCornerCropShape(id = 0, title = "Rounded"))
                )
            }

            OutlineType.CutCorner -> {
                CutCornerRectOutlineContainer(
                    outlines = listOf(CutCornerCropShape(id = 0, title = "CutCorner"))
                )
            }

            OutlineType.Oval -> {
                OvalOutlineContainer(
                    outlines = listOf(OvalCropShape(id = 0, title = "Oval"))
                )
            }

            OutlineType.Polygon -> {
                PolygonOutlineContainer(
                    outlines = listOf(
                        PolygonCropShape(
                            id = 0,
                            title = "Polygon"
                        ),
                        PolygonCropShape(
                            id = 1,
                            title = "Pentagon",
                            polygonProperties = PolygonProperties(sides = 5, 0f),
                            shape = createPolygonShape(5, 0f)
                        ),
                        PolygonCropShape(
                            id = 2,
                            title = "Heptagon",
                            polygonProperties = PolygonProperties(sides = 7, 0f),
                            shape = createPolygonShape(7, 0f)
                        ),
                        PolygonCropShape(
                            id = 3,
                            title = "Octagon",
                            polygonProperties = PolygonProperties(sides = 8, 0f),
                            shape = createPolygonShape(8, 0f)
                        )
                    )
                )
            }

            OutlineType.Custom -> {
                CustomOutlineContainer(
                    outlines = listOf(
                        CustomPathOutline(id = 0, title = "Custom", path = Paths.Favorite),
                        CustomPathOutline(id = 1, title = "Star", path = Paths.Star)
                    )
                )
            }

            OutlineType.ImageMask -> {

                val outlines = defaultImages.mapIndexed { index, image ->
                    ImageMaskOutline(id = index, title = "ImageMask", image = image)

                }
                ImageMaskOutlineContainer(
                    outlines = outlines
                )
            }
        }
    }

    fun editCropFrame(cropFrame: CropFrame) {
        val indexOf = cropFrames.indexOfFirst { it.outlineType == cropFrame.outlineType }
        cropFrames[indexOf] = cropFrame
    }
}
