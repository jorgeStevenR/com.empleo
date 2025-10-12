package com.portalempleos.controller;

import com.portalempleos.model.User;
import com.portalempleos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Permite peticiones desde el frontend
public class UserController {

    @Autowired
    private UserService userService;

    // 🔹 Obtener todos los usuarios
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // 🔹 Registrar usuario
    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return userService.registerUser(user);
    }

    // 🔹 Iniciar sesión
    @PostMapping("/login")
    public String loginUser(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        User user = userService.getUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return "✅ Login exitoso para: " + user.getName();
        } else {
            return "❌ Credenciales incorrectas";
        }
    }

    // 🔹 Eliminar usuario
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "Usuario eliminado correctamente";
    }
}
