package Training4;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.OmikujiDAO;
import dao.ResultDAO;

/**
 * Servlet implementation class checkServlet
 */
@WebServlet("/checkServlet")
public class checkServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public checkServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		//フォワードをするための準備
		RequestDispatcher requestDispatcher = null;

		//日付入力のフォーマットの宣言
		SimpleDateFormat simpleDateFormat = null;

		//現在の日付を準備
		Date date = null;

		//ランダムクラスの準備
		Random rand = null;

		//おみくじの宣言
		Omikuji omikuji = null;

		try {

			//リクエストパラメータを取得
			request.setCharacterEncoding("UTF-8");
			String birthday = request.getParameter("birthday");

			//日付入力のフォーマット（yyyyMMdd）の生成
			simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

			//入力された値が正しい（存在している）かチェック
			simpleDateFormat.setLenient(false);

			//入力された値が形式にあっていたらDate型に変換
			if (birthday.matches("^[0-9]{4}[0-9]{2}[0-9]{2}$")) {

				//フォームに入力した日付をDate型に変換
				Date inputBirthday = simpleDateFormat.parse(birthday);

				//入力した日付をフォーマット
				birthday = simpleDateFormat.format(inputBirthday);

				//Date型（現在）の生成
				date = new Date();

				//Date型(現在)をString型に変換
				String now = new SimpleDateFormat("yyyyMMdd").format(date);

				//ランダムオブジェクトを生成する（本日と入力した値のString型をInteger型に変換）
				rand = new Random(Integer.parseInt(now) + Integer.parseInt(birthday));

				//OmikujiDAOのオブジェクト生成
				OmikujiDAO omikujiDao = new OmikujiDAO();

				//ランダムオブジェクトをOmikujiDAOに渡す
				omikuji = omikujiDao.findOmikuji(rand);

				//ResultDAOのオブジェクト生成
				ResultDAO resultDao= new ResultDAO();

				//今日と、入力した日付型と、おみくじオブジェクトからおみくじコードを渡す
				resultDao.insertResult(date, inputBirthday, omikuji.getOmikujiCode());

				//おみくじの結果をresult.jspに渡す
				request.setAttribute("omikuji", omikujiDao.findOmikuji(rand));

				//result.jspに遷移する
				requestDispatcher = request.getRequestDispatcher("/jsp/result.jsp");
				requestDispatcher.forward(request, response);


			} else {
				//正規表現でない場合のエラーメッセージ
				request.setAttribute("errMsg", "入力形式がyyyyMMddではないです");

				//正規表現の入力チェックで弾かれたら入力画面に戻る
				requestDispatcher = request.getRequestDispatcher("/inputBirthday");
				requestDispatcher.forward(request, response);
			}

		} catch (ParseException pe) {
			//存在しない日付の場合のエラーメッセージ
			request.setAttribute("errMsg", "存在しない日付です");

			//存在しない日付の場合も、入力画面に戻る
			requestDispatcher = request.getRequestDispatcher("/inputBirthday");
			requestDispatcher.forward(request, response);
		}
	}
}
