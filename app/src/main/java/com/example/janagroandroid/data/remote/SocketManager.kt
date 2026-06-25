package com.example.janagroandroid.data.remote

import android.util.Log
import com.example.janagroandroid.data.local.SessionManager
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.client.Ack
import io.socket.engineio.client.transports.WebSocket
import org.json.JSONObject
import java.net.URISyntaxException

class SocketManager(private val sessionManager: SessionManager) {
    private var mSocket: Socket? = null

    companion object {
        private const val TAG = "SocketManager"
//        private const val SOCKET_URL = "http://10.36.186.117:3000"
      private const val SOCKET_URL = "http://10.0.2.2:3000"
        // Emulator: gunakan "http://10.0.2.2:3000"
    }

    fun connect() {
        if (mSocket?.connected() == true) return

        try {
            val token = sessionManager.getToken() ?: return
            val opts = IO.Options().apply {
                forceNew = true
                reconnection = true
                transports = arrayOf(WebSocket.NAME)
                extraHeaders = mapOf("Authorization" to listOf("Bearer $token"))
            }

            mSocket = IO.socket(SOCKET_URL, opts)

            mSocket?.on(Socket.EVENT_CONNECT) {
                Log.d(TAG, "Socket Connected")
            }

            mSocket?.on(Socket.EVENT_DISCONNECT) {
                Log.d(TAG, "Socket Disconnected")
            }

            mSocket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
                val error = if (args != null && args.isNotEmpty()) args[0] else "Unknown error"
                Log.e(TAG, "Connect Error: $error")
            }

            mSocket?.connect()
        } catch (e: URISyntaxException) {
            Log.e(TAG, "URISyntaxException: ${e.message}")
        }
    }

    fun disconnect() {
        mSocket?.disconnect()
        mSocket?.off()
    }

    fun getSocket(): Socket? = mSocket

    fun sendMessage(receiverId: Long, message: String) {
        val data = JSONObject().apply {
            put("receiverId", receiverId)
            put("message", message)
        }
        
        mSocket?.emit("sendMessage", data, Ack { args ->
            val response = if (args != null && args.isNotEmpty()) args[0] as? JSONObject else null
            Log.d(TAG, "Send Message Response: $response")
        })
    }

    fun joinConversation(partnerId: Long) {
        val data = JSONObject().apply {
            put("partnerId", partnerId)
        }
        mSocket?.emit("joinConversation", data)
    }

    fun markAsDelivered(messageId: Long) {
        if (messageId == 0L) return
        val data = JSONObject().apply {
            put("messageId", messageId)
        }
        mSocket?.emit("messageDelivered", data)
    }

    fun markAsRead(messageId: Long) {
        if (messageId == 0L) return
        val data = JSONObject().apply {
            put("messageId", messageId)
        }
        mSocket?.emit("messageRead", data)
    }
}
