package com.tkido.appcheck

object Processor {
  import com.tkido.tools.Logger
  
  def apply(target:Target) :String = {
    Logger.debug(target)
    
    val result = Checker(target)
    Logger.debug(result)
    
    val results = ResultParser(target)
    Logger.debug(results)
    
    val results_to_report = (
	  if(results.isEmpty || result.rank != results.head.rank){
	    val new_results = result :: results
	    ResultWriter(target, new_results)
	    new_results
	  }else{
	    results
	  }
	).take(4)
	
	"""<h3>%s</h3>""".format(target.name) ++
	results_to_report
	  .map(r => """<tr><td>%s</td><td>%s</td></tr>""".format(r.rank, r.updated))
	  .mkString("""<table border="2"><tbody><tr><th>順位</th><th>時刻</th></tr>""", "\n", "\n</tbody></table>")
  }
  
}