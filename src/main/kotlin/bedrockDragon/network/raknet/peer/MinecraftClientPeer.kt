package bedrockDragon.network.raknet.peer

import bedrockDragon.network.auth.MojangAuth
import bedrockDragon.reactive.player.PlayerObservable
import bedrockDragon.reactive.ReactSocket
import com.nimbusds.jose.JWSObject
import io.reactivex.rxjava3.core.Observable


class MinecraftClientPeer(val protocol: Int, val playerData: List<JWSObject>, val skinData: String,
                          override var observable: Observable<Any>): ReactSocket<PlayerObservable>, MinecraftPeer() {
    //TODO() class for playerData
    var xuid: Long = 0
    var uuid: String = ""
    var userName: String = ""
    init {
        for(jwt in playerData) {
            val jsonJwt = jwt.payload.toJSONObject()
            if(jsonJwt.containsKey("extraData")) {
                //very unsafe checks here
                //TODO
                val extra = jsonJwt["extraData"] as Map<*,*>
                xuid = (extra["XUID"] as String).toLong()
                uuid = extra["identity"] as String
                userName = extra["displayName"] as String
            }
        }

        var statusPreObservable: PlayerStatus = PlayerStatus.Connected

        if(MojangAuth.verifyXUIDFromChain(playerData)) {
            statusPreObservable = PlayerStatus.Authenticated
        }

        if(userName.length !in 3..16)
         {
             statusPreObservable = PlayerStatus.PendDisconnect
        }

        if(!userName.matches(Regex("^a-zA-Z0-9_]*$"))) {
            statusPreObservable = PlayerStatus.PendDisconnect
        }

        if(statusPreObservable == PlayerStatus.Authenticated) {
            statusPreObservable = PlayerStatus.LoadingGame
        }

        //publish our new status
        observable = Observable.just(statusPreObservable)
        observable.publish()

        //send back decoded salt to complete the encryption chain

    }
}

enum class PlayerStatus {
    PendDisconnect,
    Connected,
    Authenticated,
    LoadingGame,
    InGame,
}
