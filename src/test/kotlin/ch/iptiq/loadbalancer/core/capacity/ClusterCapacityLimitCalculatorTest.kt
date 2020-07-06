package ch.iptiq.loadbalancer.core.capacity

import ch.iptiq.loadbalancer.model.Provider
import ch.iptiq.loadbalancer.model.ProviderStatus
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ClusterCapacityLimitCalculatorTest {

    private val capacityLimitCalculator = ClusterCapacityLimitCalculator()

    private val capacity = 5
    private val provider1 = Provider(capacity)
    private val provider2 = Provider(capacity)
    private val provider3 = Provider(capacity)

    @Test
    fun shouldCalculateCorrectCapacityWhenAllProvidersAreActive() {
        val totalCapacity = capacityLimitCalculator.calculate(listOf(provider1, provider2, provider3))
        assert(totalCapacity == 15)
    }

    @Test
    fun shouldCalculateCorrectCapacityWhenSomeProvidersAreActive() {
        provider1.status = ProviderStatus.NOT_ACTIVE
        provider2.status = ProviderStatus.NOT_ACTIVE
        val totalCapacity = capacityLimitCalculator.calculate(listOf(provider1, provider2, provider3))
        assert(totalCapacity == 5)
    }
}