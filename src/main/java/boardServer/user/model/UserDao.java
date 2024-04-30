package boardServer.user.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import boardServer.util.DBManager;
import boardServer.util.PasswordCrypto;

public class UserDao {

	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;

	// UserDao 객체를 단일 인스턴스로 만들기 위해
	// Singleton Pattern 적용

	// 1. 생성자를 private으로
	private UserDao() {}

	// 2. 단일 인스턴스를 생성 (클래스 내부에서)
	private static UserDao instance = new UserDao();

	// 3. 단일 인스턴스에 대한 getter
	public static UserDao getInstance() {
		return instance;
	}

	public List<UserResponseDto> findUserAll() {
		List<UserResponseDto> list = new ArrayList<UserResponseDto>();

		try {
			conn = DBManager.getConnection();
			
			// 쿼리할 준비
			String sql = "SELECT id, email, name, birth, gender, country, telecom, phone, agree FROM users";
			pstmt = conn.prepareStatement(sql);

			// 쿼리 실행
			rs = pstmt.executeQuery();

			// 튜플 읽기
			while (rs.next()) {
				// database의 column index는 1부터 시작!
				String id = rs.getString(1);
				String email = rs.getString(2);
				String name = rs.getString(3);
				String birth = rs.getString(4);
				String gender = rs.getString(5);
				String country = rs.getString(6);
				String telecom = rs.getString(7);
				String phone = rs.getString(8);
				boolean agree = rs.getBoolean(9);

				UserResponseDto user = new UserResponseDto(id, email, name, birth, gender, country, telecom, phone,
						agree);
				list.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public UserResponseDto findUserByIdAndPassword(String id, String password) {
		UserResponseDto user = null;
		
		// 데이터베이스에 있는 암호화된 패스워드 str를 얻어와
		// PasswordCrypto.decrypt(str) 를 통해
		// 일치 여부 확인 후
		// return
		
		try {
			conn = DBManager.getConnection();
			
			String sql = "SELECT id, email, name, birth, gender, country, telecom, phone, agree, password FROM users WHERE id=?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				String email = rs.getString(2);
				String name = rs.getString(3);
				String birth = rs.getString(4);
				String gender = rs.getString(5);
				String country = rs.getString(6);
				String telecom = rs.getString(7);
				String phone = rs.getString(8);
				boolean agree = rs.getBoolean(9);
				String encryptedPassword = rs.getString(10);
				
				if(PasswordCrypto.decrypt(password, encryptedPassword)) {
					user = new UserResponseDto(id, email, name, birth, gender, country, telecom, phone, agree);					
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}

	public UserResponseDto createUser(UserRequestDto userDto) {
		// sql 구문을 쿼리하고
		// 성공을 했다면 -> UserResponseDto 객체 생성하여
		// 반환

		try {
			conn = DBManager.getConnection();
			
			String sql = "INSERT INTO users(id, password, email, name, birth, gender, country, telecom, phone, agree) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			pstmt = conn.prepareStatement(sql);

			// sql 구문에 맵핑할 값 설정
			pstmt.setString(1, userDto.getId());
			pstmt.setString(2, PasswordCrypto.encrypt(userDto.getPassword()));
			
			String email = userDto.getEmail().equals("") ? null : userDto.getEmail();
			pstmt.setString(3, email);

			pstmt.setString(4, userDto.getName());
			pstmt.setString(5, userDto.getBirth());
			pstmt.setString(6, userDto.getGender());
			pstmt.setString(7, userDto.getCountry());
			pstmt.setString(8, userDto.getTelecom());
			pstmt.setString(9, userDto.getPhone());
			pstmt.setBoolean(10, userDto.isAgree());

			pstmt.execute();

			return findUserByIdAndPassword(userDto.getId(), userDto.getPassword());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public UserResponseDto updateUserPassword(UserRequestDto userDto, String newPassword) {
		UserResponseDto user = null;

		if (newPassword == null || newPassword.equals("")) {
			return user;
		}

		try {
			if(findUserByIdAndPassword(userDto.getId(), userDto.getPassword()) == null)
				return user;
			
			String sql = "UPDATE users SET password=? WHERE id=?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, PasswordCrypto.encrypt(newPassword));
			pstmt.setString(2, userDto.getId());
			
			pstmt.execute();

			User userVo = findUserById(userDto.getId());
			user = new UserResponseDto(userVo);
			return user;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}

	public UserResponseDto updateUserEmail(UserRequestDto userDto) {
		UserResponseDto user = null;
		
		try {
			conn = DBManager.getConnection();
			
			if(findUserByIdAndPassword(userDto.getId(), userDto.getPassword()) == null)
				return user;
			
			String sql = "UPDATE users SET email=? WHERE id=?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userDto.getEmail());
			pstmt.setString(2, userDto.getId());

			pstmt.execute();
			
			user = findUserByIdAndPassword(userDto.getId(), userDto.getPassword());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}

	public UserResponseDto updateUserPhone(UserRequestDto userDto) {
		UserResponseDto user = null;
		
		try {
			conn = DBManager.getConnection();
			
			if(findUserByIdAndPassword(userDto.getId(), userDto.getPassword()) == null)
				return user;
			
			String sql = "UPDATE users SET telecom=?, phone=? WHERE id=?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userDto.getTelecom());
			pstmt.setString(2, userDto.getPhone());
			pstmt.setString(3, userDto.getId());

			pstmt.execute();
			
			user = findUserByIdAndPassword(userDto.getId(), userDto.getPassword());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}

	public boolean deleteUser(UserRequestDto userDto) {
		if(findUserByIdAndPassword(userDto.getId(), userDto.getPassword()) == null) {
			return false;
		}
		
		try {
			String sql = "DELETE FROM users WHERE id=? AND password=?";
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, userDto.getId());
			pstmt.setString(2, userDto.getPassword());

			pstmt.execute();

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	private User findUserById(String id) {
		User user = null;

		try {
			conn = DBManager.getConnection();
			
			String sql = "SELECT id, email, name, birth, gender, country, telecom, phone, agree, reg_date, mod_date FROM users WHERE id=?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				String email = rs.getString(2);
				String name = rs.getString(3);
				String birth = rs.getString(4);
				String gender = rs.getString(5);
				String country = rs.getString(6);
				String telecom = rs.getString(7);
				String phone = rs.getString(8);
				boolean agree = rs.getBoolean(9);
				Timestamp regDate = rs.getTimestamp(10);
				Timestamp modDate = rs.getTimestamp(11);

				user = new User(id, email, name, birth, gender, country, telecom, phone, agree, regDate, modDate);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}
}