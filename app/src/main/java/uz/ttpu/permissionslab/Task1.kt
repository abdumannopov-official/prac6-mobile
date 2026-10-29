package uz.ttpu.permissionslab

class PermissionModel {
    var granted: Boolean = false
        private set
    var rationale: Boolean = false
        private set
    var dialogShows: Boolean = true
        private set

    private var denialCount: Int = 0

    fun request(userAllows: Boolean) {
        if (!dialogShows) {
            println("Request: no dialog, denied immediately")
            return
        }

        if (userAllows) {
            granted = true
            rationale = false
            dialogShows = true
            println("Request: User allowed permission")
        } else {
            denialCount++
            granted = false

            if (denialCount == 1) {
                rationale = true
                dialogShows = true
                println("Request: Denied once")
            } else if (denialCount >= 2) {
                rationale = false
                dialogShows = false
                println("Request: Denied twice (permanently denied)")
            }
        }
    }

    fun printState() {
        println("State -> granted: $granted, rationale: $rationale, dialogShows: $dialogShows\n")
    }
}

fun main() {
    val model = PermissionModel()

    println("=== Step 1: First request (User denies) ===")
    model.request(userAllows = false)
    model.printState()

    println("=== Step 2: Second request (User denies again) ===")
    model.request(userAllows = false)
    model.printState()

    println("=== Step 3: Third request (After two denials) ===")
    model.request(userAllows = true)
    model.printState()

    println("=== Step 4: Fourth request ===")
    model.request(userAllows = true)
    model.printState()
}