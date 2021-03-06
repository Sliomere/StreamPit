package tk.sliomere.streampit.cards

import android.content.Context
import android.graphics.Color
import org.json.JSONObject
import tk.sliomere.streampit.CardAction
import tk.sliomere.streampit.MainActivity
import tk.sliomere.streampit.R

class ToggleMuteCard(id: String, name: String, color: Int, icon: String, target: String) : Card(id, name, color, icon, CardAction.TOGGLE_MUTE, target) {

    constructor(id: String, jsonObject: JSONObject) : this(id, jsonObject.getString("name")!!, Color.parseColor(jsonObject.getString("color")!!), jsonObject.getString("icon"), jsonObject.getString("target"))

    override fun onClickListener(context: Context): Boolean {
        if (!super.onClickListener(context)) {
            val args = JSONObject()
            args.put("source", target)
            MainActivity.webSocketClient.sendMessage("ToggleMute", args)

            vh.toggleMute()
        }
        return true
    }

    private var bindCallbackMsg: JSONObject? = null

    override fun reloadCard() {
        val args1 = JSONObject()
        args1.put("source", "Desktop Audio")
        MainActivity.webSocketClient.sendMessage("GetMute", args1, callback = { msg: JSONObject ->
            try {
                doReload(msg)
            } catch (e: Exception) {
                this.bindCallbackMsg = msg
            }
        })
    }

    override fun bindCompleted() {
        if (bindCallbackMsg != null) {
            doReload(bindCallbackMsg!!)
            bindCallbackMsg = null
        }
    }

    private fun doReload(msg: JSONObject) {
        val cl: Int
        icon = if (msg.getBoolean("muted")) {
            cl = vh.view.resources.getColor(R.color.colorMuted, vh.view.context.theme)
            "icon_mute"
        } else {
            cl = vh.view.resources.getColor(android.R.color.white, vh.view.context.theme)
            "icon_volume"
        }
        vh.icon.setImageDrawable(vh.view.context.resources.getDrawable(vh.view.context.resources.getIdentifier(icon, "drawable", "tk.sliomere.streampit"), vh.view.context.theme))
        vh.icon.setColorFilter(cl)
    }

}