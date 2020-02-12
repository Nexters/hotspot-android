package com.example.hotspot

import com.squareup.otto.Bus


class BusProvider {
    companion object {
        private final val BUS = Bus()
        fun getInstance() : Bus {
            return BUS
        }
    }

    private fun BusProvider() {
        // No instances.
    }
}



