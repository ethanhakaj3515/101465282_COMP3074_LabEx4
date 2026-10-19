package ca.georgebrown.comp3074.labex4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val dao by lazy { CustomerDatabase.get(applicationContext).customerDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val customers by dao.observeAll().collectAsState(initial = emptyList())
                var name by remember { mutableStateOf("") }
                var age by remember { mutableStateOf("") }
                var active by remember { mutableStateOf(true) }
                var message by remember { mutableStateOf("") }

                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Customer Directory", style = MaterialTheme.typography.headlineMedium)
                        OutlinedTextField(name, { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        OutlinedTextField(age, { age = it.filter(Char::isDigit) }, label = { Text("Age") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = active, onCheckedChange = { active = it })
                            Text("Active customer")
                        }
                        Button(onClick = {
                            val parsedAge = age.toIntOrNull()
                            if (name.isBlank() || parsedAge == null || parsedAge !in 0..130) {
                                message = "Enter a name and an age from 0 to 130."
                            } else {
                                lifecycleScope.launch {
                                    dao.insert(Customer(name = name.trim(), age = parsedAge, isActive = active))
                                    name = ""
                                    age = ""
                                    active = true
                                    message = "Customer added."
                                }
                            }
                        }) { Text("Add customer") }
                        if (message.isNotBlank()) Text(message, color = MaterialTheme.colorScheme.primary)
                        Text("Customers (${customers.size})", style = MaterialTheme.typography.titleLarge)
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(customers, key = { it.id }) { customer ->
                                Card(Modifier.fillMaxWidth()) {
                                    Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Column {
                                            Text(customer.name, style = MaterialTheme.typography.titleMedium)
                                            Text("Age ${customer.age} · ${if (customer.isActive) "Active" else "Inactive"}")
                                        }
                                        Button(onClick = { lifecycleScope.launch { dao.deleteById(customer.id) } }) { Text("Remove") }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
