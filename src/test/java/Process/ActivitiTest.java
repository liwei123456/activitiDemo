package Process;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:activiti-context.xml",  
        "classpath*:applicationContext.xml","classpath*:mybatis-config.xml",
        "classpath*:spring-mvc.xml"})
public class ActivitiTest {

	//------------------------------------部署流程start-----------------------------------
	/** 
	 * 部署流程定义 act_re_deployment
	 */  
	@Test
	public void deploymentProcessDefinition() {
	    //创建核心引擎对象 
	    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	    Deployment deployment = processEngine.getRepositoryService()// 与流程定义和部署对象相关的service  
	            .createDeployment()// 创建一个部署对象  
	            .name("activitiemployeeProcess")// 添加部署的名称  
	            .addClasspathResource("activiti/activitiEmployeeProcess.bpmn")// classpath的资源中加载，一次只能加载一个文件    
	            .addClasspathResource("activiti/activitiEmployeeProcess.png")// classpath的资源中加载，一次只能加载一个文件    
	            .deploy();// 完成部署  
	    System.out.println("部署ID:" + deployment.getId());  
	    System.out.println("部署名称：" + deployment.getName());  
	}
	
	/** 
	 * 部署流程定义 zip 
	 */  
	@Test  
	public void deploymentProcessDefinition_zip() {  
		//创建核心引擎对象 
	    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	    InputStream in = this.getClass().getClassLoader()  
	            .getResourceAsStream("activiti/activitiEmployeeProcess.zip");
	    ZipInputStream zipInputStream = new ZipInputStream(in);  
	    Deployment deployment = processEngine.getRepositoryService()// 与流程定义和部署对象相关的service  
	            .createDeployment()// 创建一个部署对象  
	            .name("流程定义")// 添加部署
	            .addZipInputStream(zipInputStream)// 指定zip格式的文件完成部署  
	            .deploy();// 完成部署  
	    System.out.println("部署ID：" + deployment.getId());  
	    System.out.println("部署名称:" + deployment.getName());  
	}
	
	//------------------------------------部署流程end-----------------------------------
	//------------------------------------流程定义start-----------------------------------
	
	/** 
	 * 查询所有的act_re_procdef流程定义表
	 */  
	@Test  
	public void findProcessDefinition() {
		//创建核心引擎对象 
	    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	    List<ProcessDefinition> list = processEngine.getRepositoryService()// 与流程定义和部署对象先相关的service  
	            .createProcessDefinitionQuery()// 创建一个流程定义的查询  
	            /** 指定查询条件，where条件 */  
	            // .deploymentId(deploymentId) //使用部署对象ID查询  
	            // .processDefinitionId(processDefinitionId)//使用流程定义ID查询  
	            // .processDefinitionNameLike(processDefinitionNameLike)//使用流程定义的名称模糊查询  
	            /* 排序 */  
	            .orderByProcessDefinitionVersion().asc()  
	            // .orderByProcessDefinitionVersion().desc()  
	            /* 返回的结果集 */  
	            .list();// 返回一个集合列表，封装流程定义  
	    // .singleResult();//返回惟一结果集  
	    // .count();//返回结果集数量  
	    // .listPage(firstResult, maxResults);//分页查询  
	    if (list != null && list.size() > 0) {  
	        for (ProcessDefinition pd : list) {  
	            System.out.println("流程定义ID:" + pd.getId());// 流程定义的key+版本+随机生成数  
	            System.out.println("流程定义的名称:" + pd.getName());// 对应helloworld.bpmn文件中的name属性值  
	            System.out.println("流程定义的key:" + pd.getKey());// 对应helloworld.bpmn文件中的id属性值  
	            System.out.println("流程定义的版本:" + pd.getVersion());// 当流程定义的key值相同的相同下，版本升级，默认1  
	            System.out.println("资源名称bpmn文件:" + pd.getResourceName());  
	            System.out.println("资源名称png文件:" + pd.getDiagramResourceName());  
	            System.out.println("部署对象ID：" + pd.getDeploymentId());  
	            System.out.println("#########################################################");  
	        }  
	    }  
	}
	
	/** 
	 * 附加功能，查询最新版本的流程定义 
	 */  
	@Test  
	public void findLastVersionProcessDefinition() {
		//创建核心引擎对象 
	    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	    List<ProcessDefinition> list = processEngine.getRepositoryService()  
	            .createProcessDefinitionQuery()  
	            .orderByProcessDefinitionVersion().asc() // 使用流程定义的版本升序排列  
	            .list();  
	  
	    /** 
	     * Map<String,ProcessDefinition> map集合的key：流程定义的key map集合的value：流程定义的对象 
	     * map集合的特点：当map集合key值相同的情况下，后一次的值将替换前一次的值 
	     */  
	    Map<String, ProcessDefinition> map = new LinkedHashMap<String, ProcessDefinition>();  
	    if (list != null && list.size() > 0) {  
	        for (ProcessDefinition pd : list) {  
	            map.put(pd.getKey(), pd);  
	        }  
	    }  
	  
	    List<ProcessDefinition> pdList = new ArrayList<ProcessDefinition>(
	            map.values());  
	    if (pdList != null && pdList.size() > 0) {  
	        for (ProcessDefinition pd : pdList) {
	            System.out.println("流程定义ID:" + pd.getId());// 流程定义的key+版本+随机生成数  
	            System.out.println("流程定义的名称:" + pd.getName());// 对应helloworld.bpmn文件中的name属性值  
	            System.out.println("流程定义的key:" + pd.getKey());// 对应helloworld.bpmn文件中的id属性值  
	            System.out.println("流程定义的版本:" + pd.getVersion());// 当流程定义的key值相同的相同下，版本升级，默认1  
	            System.out.println("资源名称bpmn文件:" + pd.getResourceName());  
	            System.out.println("资源名称png文件:" + pd.getDiagramResourceName());  
	            System.out.println("部署对象ID：" + pd.getDeploymentId());  
	            System.out  
	                    .println("#########################################################");  
	        }  
	    }  
	  
	}
	
	/** 
	 * 查看流程图 
	 */  
	@Test  
	public void viewPic() throws IOException {
		//创建核心引擎对象 
	    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	    // 将生产的图片放到文件夹下  
	    String deploymentId = "401";
	    // 获取图片资源名称  
	    List<String> list = processEngine.getRepositoryService()  
	            .getDeploymentResourceNames(deploymentId);  
	    // 定义图片资源名称  
	    String resourceName = "";  
	    if (list != null && list.size() > 0) {  
	        for (String name : list) {  
	            if (name.indexOf(".png") >= 0) {  
	                resourceName = name;  
	            }  
	        }  
	    }  
	    // 获取图片的输入流  
	    InputStream in = processEngine.getRepositoryService()  
	            .getResourceAsStream(deploymentId, resourceName);  
	    File file = new File("D:/" + resourceName);  
	    // 将输入流的图片写到D盘下  
	    FileUtils.copyInputStreamToFile(in, file);  
	}
	
	/** 
	 * 删除流程定义(删除key相同的所有不同版本的流程定义) 
	 */  
	@Test  
	public void delteProcessDefinitionByKey() {
		//创建核心引擎对象 
	    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	    // 流程定义的Key  
	    String processDefinitionKey = "activitiemployeeProcess";
	    // 先使用流程定义的key查询流程定义，查询出所有的版本  
	    List<ProcessDefinition> list = processEngine.getRepositoryService()  
	            .createProcessDefinitionQuery()  
	            .processDefinitionKey(processDefinitionKey)// 使用流程定义的key查询  
	            .list();  
	    // 遍历，获取每个流程定义的部署ID  
	    if (list != null && list.size() > 0) {  
	        for (ProcessDefinition pd : list) {  
	            // 获取部署ID  
	            String deploymentId = pd.getDeploymentId();  
	            /**  
                 * 不带级联的删除， 只能删除没有启动的流程，如果流程启动，就会抛出异常  
                 */
                //processEngine.getRepositoryService().deleteDeployment(deploymentId); 
	            /** 
	             * 级联删除 不管流程是否启动，都可以删除 
	             */
	            processEngine.getRepositoryService().deleteDeployment(deploymentId, true);  
	        }
	    }
	}
	
	//------------------------------------流程定义end-----------------------------------
	
	/**
	 * 启动流程实例
	 * 1)在数据库的act_ru_execution正在执行的执行对象表中插入一条记录
     * 2)在数据库的act_hi_procinst程实例的历史表中插入一条记录
     * 3)在数据库的act_hi_actinst活动节点的历史表中插入一条记录
     * 4)我们图中节点都是任务节点，所以同时也会在act_ru_task流程实例的历史表添加一条记录
     * 5)在数据库的act_hi_taskinst任务历史表中也插入一条记录。
	 */
	@Test  
	public void startProcessInstance() {
		//创建核心引擎对象 
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	    // 流程定义的key  
	    String processDefinitionKey = "activitiemployeeProcess";
	    ProcessInstance pi = processEngine.getRuntimeService()// 于正在执行的流程实例和执行对象相关的Service  
	            .startProcessInstanceByKey(processDefinitionKey);// 使用流程定义的key启动流程实例，key对应hellworld.bpmn文件中id的属性值，使用key值启动，默认是按照最新版本的流程定义启动  
	    System.out.println("流程实例ID:" + pi.getId());// 流程实例ID 101  
	    System.out.println("流程定义ID:" + pi.getProcessDefinitionId()); // 流程定义ID HelloWorld:1:4
	}
	
	/** 
	 * 查询历史流程实例 
	 */  
	@Test  
	public void findHistoryProcessInstance(){
		//创建核心引擎对象 
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	    String processInstanceId="45001";
	    HistoricProcessInstance hpi = processEngine.getHistoryService()  
	            .createHistoricProcessInstanceQuery()  
	            .processInstanceId(processInstanceId)  
	            .singleResult();  
	    System.out.println(hpi.getId() +"    "+hpi.getProcessDefinitionId()+"   "+ hpi.getStartTime()+"   "+hpi.getDurationInMillis());  
	}
	
	/** 
	 * 查询当前人的个人任务 
	 */  
	@Test  
	public void findMyPersonTask() {
		//创建核心引擎对象
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//	    String assignee = "zhangsan";
//	    List<Task> list = processEngine.getTaskService()// 与正在执行的认为管理相关的Service
//	            .createTaskQuery()// 创建任务查询对象
//	            .taskAssignee(assignee)// 指定个人认为查询，指定办理人
//	            .list();
		List<Task> list= processEngine.getTaskService().createTaskQuery()
				 // 根据用户id查询
				.taskCandidateGroup("wangba").list();
	    if (list != null && list.size() > 0) { 
	        for (Task task:list) {
	            System.out.println("任务ID:"+task.getId());  
	            System.out.println("任务名称:"+task.getName());  
	            System.out.println("任务的创建时间"+task);  
	            System.out.println("任务的办理人:"+task.getAssignee());  
	            System.out.println("流程实例ID:"+task.getProcessInstanceId());  
	            System.out.println("执行对象ID:"+task.getExecutionId());  
	            System.out.println("流程定义ID:"+task.getProcessDefinitionId());  
	            System.out.println("#################################");  
	        }
	    }
	}
	
	/** 
	 * 完成我的任务 
	 */  
	@Test  
	public void completeMyPersonTask(){  
		//创建核心引擎对象 
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	    //任务Id  
	    String taskId="104";  
	    processEngine.getTaskService()//与正在执行的认为管理相关的Service  
	            .complete(taskId);  
	    System.out.println("完成任务:任务ID:"+taskId);  
	}
	
	/** 
	 * 查询历史任务 
	 */  
	@Test  
	public void findHistoryTask(){
		//创建核心引擎对象 
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	    String processInstanceId="501";  
	    List<HistoricTaskInstance> list = processEngine.getHistoryService()//与历史数据（历史表）相关的service  
	            .createHistoricTaskInstanceQuery()//创建历史任务实例查询  
	            .processInstanceId(processInstanceId)  
//	              .taskAssignee(taskAssignee)//指定历史任务的办理人  
	            .list();  
	    if(list!=null && list.size()>0){  
	        for(HistoricTaskInstance hti:list){  
	            System.out.println(hti.getId()+"    "+hti.getName()+"    "+hti.getProcessInstanceId()+"   "+hti.getStartTime()+"   "+hti.getEndTime()+"   "+hti.getDurationInMillis());  
	            System.out.println("################################");  
	        }  
	    }     
	} 
	
	/** 
	 * 查询流程状态（判断流程正在执行，还是结束） 
	 */  
	@Test  
	public void isProcessEnd(){  
		//创建核心引擎对象 
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	    String processInstanceId =  "501";  
	    ProcessInstance pi = processEngine.getRuntimeService()//表示正在执行的流程实例和执行对象  
	            .createProcessInstanceQuery()//创建流程实例查询  
	            .processInstanceId(processInstanceId)//使用流程实例ID查询  
	            .singleResult();  
	    if(pi==null){  
	        System.out.println("流程已经结束");  
	    }  
	    else{  
	        System.out.println("流程没有结束");  
	    }  
	}
	
	/** 
	 * 设置流程变量 
	 */  
	@Test  
	public void setVariables() {  
		//创建核心引擎对象 
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	    // 与任务相关的service,正在执行的service  
	    TaskService taskService = processEngine.getTaskService();  
	    // 任务ID  
	    String taskId = "804";  
	    // 1.设置流程变量，使用基本数据类型  
	    taskService.setVariable(taskId, "请假天数", 7);// 与任务ID邦德  
	    taskService.setVariable(taskId, "请假日期", new Date());  
	    taskService.setVariableLocal(taskId, "请假原因", "回去探亲，一起吃个饭123");  
	    System.out.println("设置流程变量成功！");
	}
	
	/** 
	 * 获取流程变量 
	 */  
	@Test  
	public void getVariables() {
		//创建核心引擎对象 
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	    // 与任务（正在执行的service）  
	    TaskService taskService = processEngine.getTaskService();  
	    // 任务Id  
	    String taskId = "804";  
	    // 1.获取流程变量，使用基本数据类型  
	    Integer days = (Integer) taskService.getVariable(taskId, "请假天数");  
	    Date date = (Date) taskService.getVariable(taskId, "请假日期");  
	    String reason = (String) taskService.getVariable(taskId, "请假原因");  
	  
	    System.out.println("请假天数：" + days);  
	    System.out.println("请假日期：" + date);  
	    System.out.println("请假原因：" + reason);  
	  
	}
}
