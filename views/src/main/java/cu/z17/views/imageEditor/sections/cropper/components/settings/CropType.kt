package cu.z17.views.imageEditor.sections.cropper.components.settings

/**
 * Type of cropping operation
 *
 * If [CropType.Static] is selected overlay is stationary, image is movable.
 * If [CropType.Dynamic] is selected overlay can be moved, resized, image is stationary.
 */
enum class CropType {
    Static, Dynamic
}