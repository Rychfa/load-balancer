package ch.iptiq.loadbalancer.model

import ch.iptiq.loadbalancer.model.ProviderStatus.ACTIVE
import ch.iptiq.loadbalancer.model.ProviderStatus.HEALTHY
import java.util.*

class Provider(val capacity: Int) {

    private val uuid: UUID = UUID.randomUUID()

    constructor() : this(1)

    var status: ProviderStatus = ACTIVE
    var health: ProviderStatus = HEALTHY
    var recoveryCounter: Int = 0


    fun get(): UUID {
        return uuid
    }

    fun check(): Boolean {
        return health == HEALTHY
    }
}