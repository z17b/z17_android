package cu.z17.views.label

import cu.z17.singledi.SingletonInitializer
import java.util.regex.Pattern

class Z17CustomEmojiManager(val emojiList: List<Pair<Int, Int>>) {
    companion object : SingletonInitializer<Z17CustomEmojiManager>()

    val emojiRegex = Pattern.compile("").toRegex()

}