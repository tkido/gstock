package com.tkido.appcheck

object Processor {
  import com.tkido.tools.Log
  
  def apply(target:Target) :String = {
    Log d target
    
    val result = Checker(target)
    Log d result
    
    val results = ResultParser(target)
    Log d results
    
    val results_to_report = (
	  if(results.isEmpty || result.rank != results.head.rank){
	    val new_results = (result :: results).take(100)
	    ResultWriter(target, new_results)
	    new_results
	  }else{
	    results
	  }
	).take(Config.limit)
	
	"""<h3>%s</h3>""".format(target.name) ++
	results_to_report
	  .map(r => """<tr><td>%s</td><td>%s</td></tr>""".format(r.rank, r.updated))
	  .mkString("""<table border="2"><tbody><tr><th>順位</th><th>時刻</th></tr>""", "\n", "\n</tbody></table>")
  }
  
}