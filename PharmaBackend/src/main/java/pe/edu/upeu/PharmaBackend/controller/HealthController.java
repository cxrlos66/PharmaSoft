package pe.edu.upeu.PharmaBackend.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;
@RestController @RequiredArgsConstructor
public class HealthController {
 private final JdbcTemplate jdbc;
 @GetMapping({"/api/health", "/api/v1/health"})
 public ResponseEntity<Map<String,Object>> verificar() {
  try {
   jdbc.queryForObject("SELECT 1 FROM DUAL",Integer.class);
   return ResponseEntity.ok(Map.of("status","UP","database","UP","timestamp",LocalDateTime.now().toString()));
  } catch(Exception ex) {
   return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
     .body(Map.of("status","DOWN","database","DOWN","timestamp",LocalDateTime.now().toString()));
  }
 }
}
