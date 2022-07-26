package bnmo.bnmoapi.api.customer;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bnmo.bnmoapi.classes.message.Message;
import bnmo.bnmoapi.classes.role.Role;
import bnmo.bnmoapi.classes.sql.users.read.UserDetailByToken;
import bnmo.bnmoapi.classes.sql.users.read.UserRoleByToken;
import bnmo.bnmoapi.classes.token.Token;
import bnmo.bnmoapi.classes.users.UserInfo;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://bnmo.herokuapp.com"}, allowCredentials = "true")
@RequestMapping("api/customer")
public class Profile {
    
    @Autowired
    private JdbcTemplate db;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        Token token = new Token(request);
        Role role = new Role(token);
        try {
            role.setPermission(db.queryForObject(new UserRoleByToken(token.value).query(), String.class));
            if (role.isCustomer()) {
                UserInfo user = db.queryForObject(new UserDetailByToken(token.value).query(), (rs, rowNum) -> new UserInfo(
                    rs.getString("nama"),
                    rs.getString("username"),
                    rs.getString("image"),
                    rs.getFloat("saldo")
                ));
                return ResponseEntity.ok(user);
            }
        } catch (Exception e) {}
        return ResponseEntity.ok(new Message("Unauthorized"));
    }
}
