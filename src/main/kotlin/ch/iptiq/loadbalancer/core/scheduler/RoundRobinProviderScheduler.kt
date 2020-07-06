package ch.iptiq.loadbalancer.core.scheduler

import ch.iptiq.loadbalancer.model.Provider
import java.util.*

class RoundRobinProviderScheduler : ProviderScheduler {

    private var callCounter: Int = 0;

    override fun get(providers: List<Provider>): UUID {
        val providerIndex = callCounter++ % providers.size
        return providers[providerIndex].get()
    }
}