package com.mr_17.vidconnect.ui.call

import com.google.firebase.auth.FirebaseAuth
import com.mr_17.vidconnect.enums.LatestEventType
import com.mr_17.vidconnect.enums.UserStatus
import com.mr_17.vidconnect.ui.home.HomeRepository
import com.mr_17.vidconnect.ui.home.models.LatestEvent
import com.mr_17.vidconnect.utils.Constants.DATABASE_REF_USERS
import com.mr_17.vidconnect.utils.Constants.NODE_LATEST_EVENT
import com.mr_17.vidconnect.utils.Constants.NODE_STATUS
import com.mr_17.vidconnect.webrtc.PeerObserver
import com.mr_17.vidconnect.webrtc.WebRtcClient
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.SurfaceViewRenderer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallRepository @Inject constructor(
    private val webRtcClient: WebRtcClient,
    private val firebaseAuth: FirebaseAuth,
    private val homeRepository: HomeRepository
): WebRtcClient.Listener {
    private var targetId: String? = null

    fun setTargetId(targetId: String) {
        this.targetId = targetId
    }

    fun initWebRtcClient(uId: String) {
        webRtcClient.listener = this
        webRtcClient.initializeWebRtcClient(uId, object : PeerObserver() {
            override fun onAddStream(p0: MediaStream?) {
                super.onAddStream(p0)
                // notify the creator of this class that there is a new stream available
            }

            override fun onIceCandidate(p0: IceCandidate?) {
                super.onIceCandidate(p0)
                p0?.let {
                    webRtcClient.sendIceCandidate(targetId!!, it)
                }
            }

            override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
                super.onConnectionChange(newState)
                if (newState == PeerConnection.PeerConnectionState.CONNECTED) {
                    changeMyStatus(UserStatus.IN_CALL)
                    clearLatestEvent()
                }
            }
        })
    }

    fun changeMyStatus(status: UserStatus) {
        DATABASE_REF_USERS
            .child(firebaseAuth.uid.toString())
            .child(NODE_STATUS)
            .setValue(status)
    }

    fun clearLatestEvent() {
        DATABASE_REF_USERS
            .child(firebaseAuth.uid.toString())
            .child(NODE_LATEST_EVENT)
            .setValue(null)
    }

    fun initLocalSurfaceView(view: SurfaceViewRenderer, isVideoCall: Boolean) {
        webRtcClient.initLocalSurfaceView(view, isVideoCall)
    }

    fun initRemoteSurfaceView(view: SurfaceViewRenderer) {
        webRtcClient.initRemoteSurfaceView(view)
    }

    fun startCall() {
        webRtcClient.call(targetId!!)
    }

    fun endCall() {
        webRtcClient.closeConnection()
        changeMyStatus(UserStatus.ONLINE)
    }

    fun sendEndCall() {
        onTransferEventToSocket(
            LatestEvent(
                type = LatestEventType.END_CALL,
                targetId = targetId!!
            )
        )
    }

    fun toggleAudio(shouldBeMuted: Boolean) {
        webRtcClient.toggleAudio(shouldBeMuted)
    }

    fun toggleVideo(shouldBeMuted: Boolean) {
        webRtcClient.toggleVideo(shouldBeMuted)
    }

    fun switchCamera() {
        webRtcClient.switchCamera()
    }

    override fun onTransferEventToSocket(data: LatestEvent) {
        homeRepository.sendMessageToOtherClient(data)
    }
}
