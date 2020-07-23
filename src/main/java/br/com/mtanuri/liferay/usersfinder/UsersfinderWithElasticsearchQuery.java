package br.com.mtanuri.liferay.usersfinder;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.IndexSearcherHelperUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.StringQuery;
import com.liferay.portal.kernel.search.generic.TermQueryImpl;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author marceltanuri
 */

public class UsersfinderWithElasticsearchQuery {

	private static final String USER_CLASS = "com.liferay.portal.kernel.model.User";
	private static final String ENTRY_CLASS_NAME = "entryClassName";
	private static final String ASSET_TAG_NAMES = "assetTagNames";

	public static List<User> findUserByTagNames(String[] tagNames) {

		try {
			SearchContext sc = new SearchContext();
			sc.setCompanyId(PortalUtil.getDefaultCompanyId());

			String[] classNames = { USER_CLASS };

			String assetTagClauseOR = buildAssetTagClauseOR(tagNames);

			sc.setEntryClassNames(classNames);

			TermQuery termQuery = new TermQueryImpl(ENTRY_CLASS_NAME, USER_CLASS);
			StringQuery stringQuery = new StringQuery(assetTagClauseOR);
			BooleanQuery booleanQuery = new BooleanQueryImpl();
			booleanQuery.add(termQuery, BooleanClauseOccur.MUST);
			booleanQuery.add(stringQuery, BooleanClauseOccur.MUST);

			_log.info("Searching with terms " + ENTRY_CLASS_NAME + ": " + USER_CLASS + " AND " + ASSET_TAG_NAMES + ": "
					+ assetTagClauseOR);

			Hits hits = IndexSearcherHelperUtil.search(sc, booleanQuery);
			Document[] userDocuments = hits.getDocs();

			if (userDocuments != null) {
				_log.info(userDocuments.length + " results were found for the query:");
				List<User> listOfUsers = new ArrayList<User>();
				for (Document document : userDocuments) {
					_log.debug(document);
					listOfUsers.add(UserLocalServiceUtil.fetchUser(Long.valueOf(document.get("userId"))));
				}
				return listOfUsers;

			} else {
				_log.info("No results were found for the query:");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String buildAssetTagClauseOR(String[] tagNames) {
		StringBuilder sb = new StringBuilder();
		sb.append(ASSET_TAG_NAMES + ":(");
		for (int i = 0; i < tagNames.length; i++) {
			sb.append("\"").append(tagNames[i]).append("\"");
			if (i < tagNames.length - 1) {
				sb.append(" OR ");
			}
		}
		return sb.append(")").toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(UsersfinderWithElasticsearchQuery.class);

}