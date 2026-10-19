package com.example.LatteList.config;

import com.example.LatteList.Modelo.Entidades.Cafe;
import com.example.LatteList.Modelo.Entidades.ListaDeCafe;
import com.example.LatteList.Modelo.Entidades.Resena;
import com.example.LatteList.Modelo.Entidades.Usuario;
import com.example.LatteList.Modelo.Enum.CostoPromedio;
import com.example.LatteList.Modelo.Enum.Etiquetas;
import com.example.LatteList.Modelo.Enum.TipoDeUsuario;
import com.example.LatteList.Modelo.Repositorios.CafeRepository;
import com.example.LatteList.Modelo.Repositorios.ListaDeCafeRepository;
import com.example.LatteList.Modelo.Repositorios.ResenaRepository;
import com.example.LatteList.Modelo.Repositorios.UserRepository;
import com.example.LatteList.Modelo.Servicios.UserService;
import com.example.LatteList.Vista.UsuarioDTOs.UsuarioRequestDTO;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Carga datos de ejemplo (usuarios, cafes, resenas y listas) para poder probar el front.
 * Es idempotente: si ya existe el dueño demo, no hace nada.
 * Para desactivarlo: app.datos-demo=false en application-local.properties.
 * Todas las cuentas demo usan la contraseña "Demo1234".
 */
@Component
@Order(2)
@ConditionalOnProperty(name = "app.datos-demo", havingValue = "true", matchIfMissing = true)
public class DatosDemo implements CommandLineRunner {

    private static final String PASS = "Demo1234";
    private static final String EMAIL_MARCA = "duenio1@lattelist.com";

    private final UserService userService;
    private final UserRepository userRepository;
    private final CafeRepository cafeRepository;
    private final ResenaRepository resenaRepository;
    private final ListaDeCafeRepository listaRepository;

    public DatosDemo(UserService userService, UserRepository userRepository, CafeRepository cafeRepository,
                     ResenaRepository resenaRepository, ListaDeCafeRepository listaRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.cafeRepository = cafeRepository;
        this.resenaRepository = resenaRepository;
        this.listaRepository = listaRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.existsByEmail(EMAIL_MARCA)) return;

        Usuario d1 = usuario("Martina", "Quiroga", EMAIL_MARCA, TipoDeUsuario.DUENIO);
        Usuario d2 = usuario("Federico", "Ledesma", "duenio2@lattelist.com", TipoDeUsuario.DUENIO);

        List<Usuario> clientes = List.of(
                usuario("Lucía", "Fernández", "cliente1@lattelist.com", TipoDeUsuario.CLIENTE),
                usuario("Tomás", "Benítez", "cliente2@lattelist.com", TipoDeUsuario.CLIENTE),
                usuario("Camila", "Ríos", "cliente3@lattelist.com", TipoDeUsuario.CLIENTE),
                usuario("Joaquín", "Pereyra", "cliente4@lattelist.com", TipoDeUsuario.CLIENTE));

        List<Cafe> cafes = List.of(
                cafe(d1, "Aroma del Puerto", "Alberti 1450", CostoPromedio.$$, "https://instagram.com/aroma.del.puerto",
                        "CAFE_ESPECIALIDAD", "TOSTADO_PROPIO", "WIFI", "ZONA_CENTRICA", "ESTUDIANTES"),
                cafe(d1, "La Taza Azul", "Colón 3231", CostoPromedio.$, null,
                        "CAFE_COMERCIAL", "WIFI", "PAREJAS", "FAMILIAR"),
                cafe(d1, "Granos de Sal", "Güemes 2640", CostoPromedio.$$$, "https://instagram.com/granos.de.sal",
                        "CAFE_ESPECIALIDAD", "BLEND_EXCLUSIVO", "MINIMALISTA", "TERRAZA", "PAREJAS"),
                cafe(d1, "Rincón Vegano", "Rivadavia 2215", CostoPromedio.$$, null,
                        "VEGANO", "VEGETARIANO", "SIN_TACC", "ORGANICO", "PET_FRIENDLY"),
                cafe(d1, "Brunch & Books", "San Luis 2020", CostoPromedio.$$, "https://instagram.com/brunch.books",
                        "BRUNCH", "LIBROS", "ENCHUFES_DISPONIBLES", "WIFI", "ESTILO_COWORKING"),
                cafe(d2, "Costa Café", "Boulevard Marítimo 2700", CostoPromedio.$$$, null,
                        "FRENTE_AL_MAR", "TERRAZA", "GRUPOS_GRANDES", "PASTELERIA_FRANCESA"),
                cafe(d2, "El Viejo Tostador", "Belgrano 2310", CostoPromedio.$$, "https://instagram.com/viejo.tostador",
                        "TOSTADO_PROPIO", "VINTAGE", "MUSICA_EN_VIVO", "CALEFACCION"),
                cafe(d2, "Dolce Vita", "Falucho 1980", CostoPromedio.$$, null,
                        "PASTELERIA_ITALIANA", "CAFE_COMERCIAL", "FAMILIAR", "AIRE_ACONDICIONADO"),
                cafe(d2, "Patio Escondido", "Mitre 2845", CostoPromedio.$, null,
                        "PATIO", "ARTE", "TEMATICO", "APTO_CELIACOS", "ESTUDIANTES"),
                cafe(d2, "Latte Lab", "Luro 3120", CostoPromedio.$$$, "https://instagram.com/latte.lab",
                        "CAFE_ESPECIALIDAD", "ORGANICO", "MINIMALISTA", "ESTILO_COWORKING", "WIFI"));

        String[] comentarios = {
                "Excelente café, volvería sin dudas.", "Muy buena atención y lindo ambiente.",
                "Un poco caro pero vale la pena.", "Ideal para ir a estudiar un rato.",
                "La pastelería es increíble.", "Buen lugar para ir en pareja.",
                "El café es correcto, nada del otro mundo.", "Me encantó el lugar, súper recomendable.",
                "Tardaron un poco en atender pero el café estaba muy rico.", "Gran variedad y precios razonables."};

        Random azar = new Random(2026);
        for (Cafe c : cafes) {
            for (Usuario cliente : clientes) {
                if (azar.nextInt(10) < 7) {           // ~70% de los clientes reseña cada café
                    Resena r = new Resena();
                    r.setCafe(c);
                    r.setUsuario(cliente);
                    r.setPuntuacionGeneral(3 + azar.nextInt(3));
                    r.setPuntuacionPrecio(2 + azar.nextInt(4));
                    r.setPuntuacionAtencion(3 + azar.nextInt(3));
                    r.setComentario(comentarios[azar.nextInt(comentarios.length)]);
                    resenaRepository.save(r);
                }
            }
        }

        // Listas: cada cliente tiene "Favoritos" (la crea UserService) y una lista propia
        for (int i = 0; i < clientes.size(); i++) {
            Usuario cliente = clientes.get(i);
            List<ListaDeCafe> listas = listaRepository.findByUsuarioIdOrderByFechaCreacionAscIdAsc(cliente.getId());
            ListaDeCafe favoritos = listas.get(0);
            favoritos.getCafes().add(cafes.get(i));
            favoritos.getCafes().add(cafes.get(i + 4));
            listaRepository.save(favoritos);

            ListaDeCafe propia = new ListaDeCafe();
            propia.setNombre(i % 2 == 0 ? "Para estudiar" : "Salidas de fin de semana");
            propia.setUsuario(cliente);
            propia.getCafes().add(cafes.get((i + 1) % cafes.size()));
            propia.getCafes().add(cafes.get((i + 2) % cafes.size()));
            propia.getCafes().add(cafes.get((i + 7) % cafes.size()));
            listaRepository.save(propia);
        }

        System.out.println("[DatosDemo] Cargados 2 dueños, 4 clientes, " + cafes.size() + " cafés, reseñas y listas. Contraseña: " + PASS);
    }

    private Usuario usuario(String nombre, String apellido, String email, TipoDeUsuario tipo) {
        return userService.crear(new UsuarioRequestDTO(nombre, apellido, email, PASS, tipo));
    }

    private Cafe cafe(Usuario duenio, String nombre, String direccion, CostoPromedio costo, String instagram, String... etiquetas) {
        Cafe c = new Cafe();
        c.setNombre(nombre);
        c.setDireccion(direccion);
        c.setCostoPromedio(costo);
        c.setInstagramUrl(instagram);
        c.setEtiquetas(resolverEtiquetas(etiquetas));
        c.setDuenio(duenio);
        return cafeRepository.save(c);
    }

    /** Convierte nombres a enum; ignora los que no existan en tu enum Etiquetas (así nunca rompe la compilación). */
    private Set<Etiquetas> resolverEtiquetas(String... nombres) {
        Set<Etiquetas> set = EnumSet.noneOf(Etiquetas.class);
        for (String n : nombres) {
            try {
                set.add(Etiquetas.valueOf(n));
            } catch (IllegalArgumentException e) {
                // etiqueta inexistente en este proyecto: se omite
            }
        }
        if (set.isEmpty()) set.add(Etiquetas.values()[0]);
        return set;
    }
}
