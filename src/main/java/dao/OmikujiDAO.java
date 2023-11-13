package dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Random;

import Training4.BadLuck;
import Training4.GreatBlessing;
import Training4.MiddleBlassing;
import Training4.Omikuji;
import Training4.SmallBlessing;
import Training4.UncertinLuck;
/**
 * Omikujiテーブルです
 * 
 * csvの読み込みとおみくじテーブルへの登録、
 * おみくじオブジェクトを生成、結果テーブルへの登録をします
 * 
 * @author e_kumakiri
 *
 */
public class OmikujiDAO {

	//データベース接続に使用する情報
	/** ドライバクラス名 */
	private static final String DRIVER = "org.postgresql.Driver";

	/**接続するDBのURL*/
	private static final String URL = "jdbc:postgresql://localhost:5432/banana";

	/**接続するためのユーザ名*/
	private static final String USER_NAME = "postgres";

	/**接続するためのパスワード*/
	private static final String PASSWORD = "kumakiri2005";
	
	//DBに接続するための宣言
	Connection connection = null;

	//PreparedStatemenrの準備
	PreparedStatement preparedStatement = null;

	//ResultSetの準備
	ResultSet resultSet = null;

	//おみくじコードの宣言
	String omikujiCode = "";

	/**
	 * csvファイルを読み込んで、おみくじテーブルに登録するクラスです
	 */
	public void insertOmikuji() {

		//パスの取得
		Path path = Paths.get("/Users/e_kumakiri/Desktop/workspace/Training4/src/main/java/Fortune.csv");

		try {

			//csvファイルの読み込み
			List<String> line = Files.readAllLines(path);

			//JDBCドライバクラスをJVMに登録
			Class.forName(DRIVER);

			//データベースへ接続
			connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);

			//接続に成功した場合に表示
			if(connection != null) {
				System.out.println("csvファイルを取り込むため、DBと接続しました");
			}

			//最大件数を呼び出す
			int countOmikujiCode = countOmikujiCode();

			//最大件数が50未満だったら登録処理にすすむ
			if(countOmikujiCode < 50) {

				//50回分する
				for (int i = 0; i < line.size(); i++) {

					//dataの,を取り除く
					String[] data = line.get(i).split(",");

					//SQL文を準備(OmikujiテーブルをINSERTする)
					String sqlInsertOmikuji = "INSERT INTO Omikuji VALUES(?, ?, ?, ?, ?, 'kumakiri', CURRENT_DATE, 'kumakiri', CURRENT_DATE);";

					//ステートメントの作成（オブジェクト生成）
					preparedStatement  = connection.prepareStatement(sqlInsertOmikuji);

					//SQL中の各プレースホルダーに入力値をバインド
					preparedStatement.setString(1, data[0]);
					//各具象クラスの戻り値をバインド
					preparedStatement.setString(2, convertUnsei(data[1]));
					preparedStatement.setString(3, data[2]);
					preparedStatement.setString(4, data[3]);
					preparedStatement.setString(5, data[4]);

					//SQL文を実行(登録の際はUpdate)
					int num = preparedStatement.executeUpdate();

					//登録された件数が1件でなければ出力
					if(num != 1) {
						System.out.println("登録されていません");
					}
				}
			}

		}catch (SQLException se) {
			//DB接続関連でのエラー
			System.out.println("DB関係でエラーです");
			se.printStackTrace();

		}catch(IOException ie) {
			//csvファイルの処理に失敗した場合
			System.out.println("csv処理でエラーです");
			ie.printStackTrace();

		}catch(ClassNotFoundException ce) {
			//ドライバクラスが見つからなかった場合
			System.out.println("ドライバクラスが見つかりません");
			ce.printStackTrace();

		}finally {
			//DBとの接続を切断
			disconnect();
			System.out.println("DBを切断しました");
		}
	}

	/**
	 * 最大件数を取得するメソッドです
	 * @return countOmikujiCode 最大件数
	 */
	public int countOmikujiCode() {
		
		//最大値の宣言
		int countOmikujiCode = 0;

		try {

			//JDBCドライバクラスをJVMに登録
			Class.forName(DRIVER);

			//データベースへ接続
			connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);

			//接続に成功した場合に表示
			if(connection != null) {
				System.out.println("最大件数を取得するため、DBと接続しました");
			}

			//最大件数を出したいSQL(INSERTの条件、ランダムの最大件数に使用)
			//AS countOmikuji 別名をつけている
			String countOmikuji = "SELECT COUNT(omikuji_code) AS countOmikuji FROM Omikuji;";
		
			//ステートメント（SQL文を受け取って実行）
			Statement statement1 = connection.createStatement();
		
			//SQL文を実行して、その結果をresultSetに代入
			resultSet = statement1.executeQuery(countOmikuji);

			if (resultSet.next()) {
				//1件取得(最大件数)
				countOmikujiCode = resultSet.getInt("countOmikuji");
			}

		}catch (SQLException se) {
			//DB接続関連でのエラー
			System.out.println("DB関係でエラーです");
			se.printStackTrace();

		}catch(ClassNotFoundException ce) {
			//ドライバクラスが見つからなかった場合
			System.out.println("ドライバクラスが見つかりません");
			ce.printStackTrace();
		}

		//最大件数を返す
		return countOmikujiCode;
	}

	/**
	 * ランダムオブジェクトを受け取り、おみくじオブジェクトを生成するメソッドです
	 * 
	 * @param rand ランダムオブジェクト
	 * @return Omikuji おみくじオブジェクト
	 */
	public Omikuji findOmikuji(Random rand) {

		//おみくじオブジェクトの宣言
		Omikuji omikuji = null;
		
		//最大値の宣言
		int countOmikujiCode = ０;

		try {

			//JDBCドライバクラスをJVMに登録
			Class.forName(DRIVER);

			//データベースへ接続
			connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
			
			//接続に成功した場合に表示
			if(connection != null) {
				System.out.println("おみくじオブジェクトを生成するため、DBと接続しました");
			}

			//最大件数を呼び出す（ランダムで使用する）
			countOmikujiCode = countOmikujiCode();

			//おみくじをランダムにするための準備(おみくじコードの最大値を上限とする)
			//おみくじコードを取得するためのSQL文(235行目で値をバインド)
			String result = "SELECT * FROM Omikuji WHERE omikuji_code = ? ";

			//ステートメント作成（オブジェクト生成）
			preparedStatement = connection.prepareStatement(result);

			/** ランダムの最小値を定義しています*/
			final int min = 1;

			//ランダムにしたおみくじの値をバインド(224行目のSQL文)
			//omikuji_codeの?の部分に、同じ日に同じ運勢が返ってくる + 50個(omikuji_codeの最大値)分ランダムにしている
			//rand.nextIntをString型に変換
			preparedStatement.setString(1, String.valueOf(rand.nextInt(countOmikujiCode) + min));

			//SQLを実行(preparedStatementのオブジェクトが代入される)
			ResultSet resultSet = preparedStatement.executeQuery();

			//next：次があるか、カーソル の役割
			if (resultSet.next()) {
				//おみくじの情報を取得
				omikujiCode = resultSet.getString("omikuji_code");
				String unseiCode = resultSet.getString("unsei_code");
				String negaigoto = resultSet.getString("negaigoto");
				String akinai = resultSet.getString("akinai");
				String gakumon = resultSet.getString("gakumon");

				//オブジェクト生成
				omikuji = getOmikuji(unseiCode);

				//値をsetする
				omikuji.setUnsei();
				omikuji.setOmikujiCode(omikujiCode);
				omikuji.setNegaigoto(negaigoto);
				omikuji.setAkinai(akinai);
				omikuji.setGakumon(gakumon);
			}

		}catch (SQLException se) {
			//DB接続関連でのエラー
			System.out.println("DB関係でエラーです");
			se.printStackTrace();

		}catch (ClassNotFoundException ce) {
			//クラスが見つからなかった時のエラー
			System.out.println("クラスが見つかりません");
			ce.printStackTrace();
		}

		//おみくじオブジェクトを返す
		return omikuji;
	}

	/**
	 * 運勢名によって返す値が変化するメソッドです
	 * 
	 * @param unseiName 運勢名
	 * @return　各具象クラスの番号
	 */
	private static String convertUnsei(String unseiName) {

		//data[1]の運勢名によって返す値が変化する
		switch (unseiName) {
		case "大吉":
			//大吉だったら01を返す
			return "01";

		case "中吉":
			return "02";

		case "小吉":
			return "03";

		case "末吉":
			return "04";

		case "吉":
			return "05";

		case "凶":
			return "06";

		default:
			//01から06以外だったら例外を投げる
			throw new IllegalArgumentException("予想外の値です");
		}
	}

	/**
	 * 運勢コードを元にオブジェクト生成をするクラスです
	 * 
	 * @param unseiCode 運勢コード
	 * @return おみくじクラス
	 */
	private static Omikuji getOmikuji(String unseiCode) {

		//取得したおみくじ情報を元にオブジェクト生成
		switch (unseiCode) {
		case "01":
			//01だったら大吉オブジェクトを生成
			return new GreatBlessing();

		case "02":
			return new MiddleBlassing();

		case "03":
			return new SmallBlessing();

		case "04":
			return new UncertinLuck();

		case "05":
			return new GreatBlessing();

		case "06":
			return new BadLuck();

		default:
			//01から06以外だったら例外を投げる
			throw new IllegalArgumentException("予想外の値です");
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
