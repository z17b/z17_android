package cu.z17.views.utils

import android.content.Context
import cu.z17.singledi.SingletonInitializer
import io.noties.markwon.Markwon
import io.noties.markwon.ext.latex.JLatexMathPlugin
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import java.util.concurrent.Executors

class Z17Markown(private val context: Context) {

    companion object : SingletonInitializer<Z17Markown>()

    private val scale = context.resources.displayMetrics.density

    val markwon = Markwon.builder(context)
        .usePlugin(StrikethroughPlugin.create())
        .usePlugin(TaskListPlugin.create(context))
        .usePlugin(HtmlPlugin.create())
        .usePlugin(LinkifyPlugin.create(true))
        .usePlugin(MarkwonInlineParserPlugin.create())
        .usePlugin(
            JLatexMathPlugin.create(
                18.0f * scale + 0.5f
            ) { builder ->

                builder.inlinesEnabled(false)

                // by default true
                builder.blocksEnabled(true)

                // executor on which parsing of LaTeX is done (by default `Executors.newCachedThreadPool()`)
                builder.executorService(Executors.newCachedThreadPool())

                builder.errorHandler { latex, error ->
                    null
                }
            })
        .build()
}