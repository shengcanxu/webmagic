1. parse based on page model
2. parse multiple pages using MultiplePagePipeline
3. parse subpages using SubpagePipeline
4. able to create db table based on page model and then save data into mysql using mysqlpipeline
5. able to format data using formatterPipeline
6. able to download file using FileDownloadPipeline
7. able to parse unlimited layer to get the content using parseurl annotation
8. add checkIfCompleteParse in scheduler to make sure all put-in-queue requests are parsed. it's used for finding out the un-parsed reqeusts becauses of network issues;
9. able to get content from the parseurl pages. means when content is gotten, they will passed along to the next level page, coming together with the final content
10. add custom function. able to use custom functions to do parse now
11. able to update and get refresh contents now. use addUrlForRefresh and setRecoverQueue of Spider
