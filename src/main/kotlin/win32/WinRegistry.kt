package win32

import java.io.BufferedReader
import java.io.InputStreamReader

class WinRegistry {

    companion object {
        const val STARTUP_ITEMS_LOCATION = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run"
        const val REGISTRY_APP_NAME = "kMonitor"

        fun read(location: String, key: String): List<String> {
            val proc = ProcessBuilder("reg", "query", location, "/v", key)
                .redirectErrorStream(true)
                .start()
            val input = BufferedReader(InputStreamReader(proc.inputStream))

            return input.lineSequence().toList()
        }

        fun isAppRegisteredToStartWithWindows(): Boolean {
            val queryOutput = read(STARTUP_ITEMS_LOCATION, REGISTRY_APP_NAME)

            return queryOutput.map { it.indexOf(REGISTRY_APP_NAME) >= 0 }.all { it }
        }
    }
}