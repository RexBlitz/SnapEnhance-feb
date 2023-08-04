package me.rhunk.snapenhance.ui.manager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import me.rhunk.snapenhance.SharedContextHolder
import me.rhunk.snapenhance.ui.AppMaterialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val startDestination = intent.getStringExtra("route")?.let { EnumSection.fromRoute(it) } ?: EnumSection.HOME
        val managerContext = SharedContextHolder.remote(this).apply {
            activity = this@MainActivity
            checkForRequirements()
        }

        val sections = EnumSection.values().toList().associateWith {
            it.section.constructors.first().call()
        }.onEach { (section, instance) ->
            with(instance) {
                enumSection = section
                context = managerContext
                init()
            }
        }

        setContent {
            val navController = rememberNavController()
            val navigation = remember { Navigation() }
            AppMaterialTheme {
                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = { navigation.NavBar(navController = navController) }
                ) { innerPadding ->
                    navigation.NavigationHost(
                        sections = sections,
                        navController = navController,
                        innerPadding = innerPadding,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}