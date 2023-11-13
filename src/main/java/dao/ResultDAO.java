package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
/**
 * resultテーブルです
 * 
 * おみくじの結果をDBに登録をします
 * 
 * @author e_kumakiri
 *
 */
public class ResultDAO {

	//データベース接続に使用する情報
	/** ドライバクラス名 */
	private static final String DRIVER = "org.postgresql.Driver";

	/**接続するDBのURL*/
	private final String URL = "jdbc:postgresql://localhost:5432/banana";

	/**接続するためのユーザ名*/
	private final String USER_NAME = "postgres";

	/**接続するためのパスワード*/
	private final String PASSWORD = "kumakiri2005";

	//DBに接続するために宣言
	Connection connection = null;

	//PreparedStatemenrの準備
	PreparedStatement preparedStatement = null;

	//ResultSetの準備
	ResultSet resultSet = null;

	/**
	 * 2つのDate型を受け取り、おみくじの結果を登録しています
	 * 
	 * @param date 現在のDate型
	 * @param inputBirthday 入力した誕生日のDate型
	 */
	public void insertResult(Date date, Date inputBirthday, String omikujiCode) {

		try {

			//JDBCドライバクラスをJVMに登録
			Class.forName(DRIVER);

			//データベースへ接続
			connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
			
			//接続に成功した場合に表示
			if(connection != null) {
				System.out.println("結果テーブルに登録するため、DBと接続しました");
			}

			//結果テーブルにおみくじの内容を登録(resultテーブルにINSERTする)
			String sqlInsertResult = "INSERT INTO result VALUES(?, ?, ?, 'kumakiri', CURRENT_DATE, 'kumakiri', CURRENT_DATE)";

			//ステートメントの作成
			PreparedStatement preparedStatement3 = connection.prepareStatement(sqlInsertResult);

			//占った日をjava.util.Dateから、java.sql.Dateへ変換
			java.sql.Date dateConvertDate = new java.sql.Date(date.getTime());

			//誕生日をjava.util.Dateから、java.sql.Dateへ変換
			java.sql.Date dateConvertInputDate = new java.sql.Date(inputBirthday.getTime());

			//SQL中の各プレースホルダーに入力値をバインド
			preparedStatement3.setDate(1, dateConvertDate); //占った日
			preparedStatement3.setDate(2, dateConvertInputDate); //誕生日
			preparedStatement3.setString(3, omikujiCode);//おみくじコードの取得

			//SQL文を実行(登録の際はUpdate)
			preparedStatement3.executeUpdate();

		}catch(SQLException se) {
			//DB関係でエラーがあった場合
			System.out.println("DB関係でエラーです");
				se.printStackTrace();

		}catch(ClassNotFoundException ce) {
			//ドライバクラスが見つからなかった場合
			System.out.println("ドライバクラスが見つかりません");
			ce.printStackTrace();

		}finally {
			//DBと接続を切断
			disconnect();
		}
	}

	/**
	 * DBとの接続を切断する処理です
	 */
	private void disconnect() {
		try {
			if(resultSet != null) {
				//nullじゃなかったら閉じる
				resultSet.close();
			}
			if(preparedStatement != null) {
				//nullじゃなかったら閉じる
				preparedStatement.close();
			}
			if(connection != null) {
				//nullじゃなかったら閉じる
				connection.close();
			}
	
		}catch(SQLException se) {
			//DB関係でエラーがあった場合
			System.out.println("DB関係でエラーです");
			se.printStackTrace();
		}
	}
}
