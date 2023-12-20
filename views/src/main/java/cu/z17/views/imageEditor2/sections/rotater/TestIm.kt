package cu.z17.views.imageEditor2.sections.rotater

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import kotlin.math.sqrt

@Composable
fun TutorialContent() {
    Column(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize()
            .padding(20.dp)
    ) {
        val painter1 = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://www.techtoyreviews.com/wp-content/uploads/2020/09/5152094_Cover_PS5.jpg")
                .size(coil.size.Size.ORIGINAL) // Set the target size to load the image at.
                .build()
        )

        Image(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
                .then(
                    if (painter1.state is AsyncImagePainter.State.Success) {
                        Modifier.drawDiagonalLabel(
                            text = "50%",
                            color = Color.Red
                        )
                    } else Modifier
                ),
            painter = painter1,
            contentScale = ContentScale.FillBounds,
            contentDescription = null
        )

        Spacer(modifier = Modifier.height(10.dp))

        val painter2 = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://i02.appmifile.com/images/2019/06/03/03ab1861-42fe-4137-b7df-2840d9d3a7f5.png")
                .size(coil.size.Size.ORIGINAL) // Set the target size to load the image at.
                .build()
        )

        Image(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
                .then(
                    if (painter2.state is AsyncImagePainter.State.Success) {
                        Modifier.drawDiagonalShimmerLabel(
                            text = "40% OFF",
                            color = Color(0xff4CAF50),
                            labelTextRatio = 5f
                        )
                    } else Modifier
                ),
            painter = painter2,
            contentScale = ContentScale.FillBounds,
            contentDescription = null
        )
    }
}


fun Modifier.drawDiagonalLabel(
    text: String,
    color: Color,
    style: TextStyle = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.White
    ),
    labelTextRatio: Float = 7f,
) = composed(
    factory = {

        val textMeasurer = rememberTextMeasurer()
        val textLayoutResult: TextLayoutResult = remember {
            textMeasurer.measure(text = AnnotatedString(text), style = style)
        }

        Modifier
            .clipToBounds()
            .drawWithContent {
                val canvasWidth = size.width
                val canvasHeight = size.height

                val textSize = textLayoutResult.size
                val textWidth = textSize.width
                val textHeight = textSize.height

                val rectWidth = textWidth * labelTextRatio
                val rectHeight = textHeight * 1.1f

                val rect = Rect(
                    offset = Offset(canvasWidth - rectWidth, 0f),
                    size = Size(rectWidth, rectHeight)
                )

                val sqrt = sqrt(rectWidth / 2f)
                val translatePos = sqrt * sqrt

                drawContent()
                withTransform(
                    {
                        rotate(
                            degrees = 45f,
                            pivot = Offset(
                                canvasWidth - rectWidth / 2,
                                translatePos
                            )
                        )
                    }
                ) {
                    drawRect(
                        color = color,
                        topLeft = rect.topLeft,
                        size = rect.size
                    )
                    drawText(
                        textMeasurer = textMeasurer,
                        text = text,
                        style = style,
                        topLeft = Offset(
                            rect.left + (rectWidth - textWidth) / 2f,
                            rect.top + (rect.bottom - textHeight) / 2f
                        )
                    )
                }

            }
    }
)


fun Modifier.drawDiagonalShimmerLabel(
    text: String,
    color: Color,
    style: TextStyle = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.White
    ),
    labelTextRatio: Float = 7f,
) = composed(
    factory = {

        val textMeasurer = rememberTextMeasurer()
        val textLayoutResult: TextLayoutResult = remember {
            textMeasurer.measure(text = AnnotatedString(text), style = style)
        }

        val transition = rememberInfiniteTransition()

        val progress by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        Modifier
            .clipToBounds()
            .drawWithContent {
                val canvasWidth = size.width
                val canvasHeight = size.height

                val textSize = textLayoutResult.size
                val textWidth = textSize.width
                val textHeight = textSize.height

                val rectWidth = textWidth * labelTextRatio
                val rectHeight = textHeight * 1.1f

                val rect = Rect(
                    offset = Offset(canvasWidth - rectWidth, 0f),
                    size = Size(rectWidth, rectHeight)
                )

                val sqrt = sqrt(rectWidth / 2f)
                val translatePos = sqrt * sqrt

                val brush = Brush.linearGradient(
                    colors = listOf(
                        color,
                        style.color,
                        color,
                    ),
                    start = Offset(progress * canvasWidth, progress * canvasHeight),
                    end = Offset(
                        x = progress * canvasWidth + rectHeight,
                        y = progress * canvasHeight + rectHeight
                    ),
                )

                drawContent()
                withTransform(
                    {
                        rotate(
                            degrees = 45f,
                            pivot = Offset(
                                canvasWidth - rectWidth / 2,
                                translatePos
                            )
                        )
                    }
                ) {
                    drawRect(
                        brush = brush,
                        topLeft = rect.topLeft,
                        size = rect.size
                    )
                    drawText(
                        textMeasurer = textMeasurer,
                        text = text,
                        style = style,
                        topLeft = Offset(
                            rect.left + (rectWidth - textWidth) / 2f,
                            rect.top + (rect.bottom - textHeight) / 2f
                        )
                    )
                }

            }
    }
)