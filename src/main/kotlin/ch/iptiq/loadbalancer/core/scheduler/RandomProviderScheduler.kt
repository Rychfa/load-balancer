package ch.iptiq.loadbalancer.core.scheduler

import ch.iptiq.loadbalancer.core.scheduler.ProviderScheduler
import ch.iptiq.loadbalancer.model.Provider
import java.util.*
import kotlin.random.Random

class RandomProviderScheduler : ProviderScheduler {
    override fun get(providers: List<Provider>): UUID {
        val providerIndex = Random.nextInt(0, providers.size)
        return providers[providerIndex].get()
    }
}