package ch.iptiq.loadbalancer.core.scheduler

import ch.iptiq.loadbalancer.model.Provider
import java.util.*

interface ProviderScheduler {
    fun get(providers : List<Provider>): UUID
}