# usersfinder-with-elasticsearch-query

Here is a Liferay DXP 7.x service that finds Users by assetTagNames.

It was used com.liferay.portal.kernel.search.IndexSearcherHelperUtil to search instead of searching via database. The service recieve an array of string (tagNames) as parameter and search for users with any of these tags (OR operator).
