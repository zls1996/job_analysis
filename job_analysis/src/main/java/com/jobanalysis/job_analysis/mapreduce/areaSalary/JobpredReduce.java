package com.jobanalysis.job_analysis.mapreduce.areaSalary;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * 计算每个城市工作总数和平均工资
 */
public class JobpredReduce extends Reducer<Text, Text, Text, Text> {
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        Integer count = 0;//计数多少个职位
        Float salary_min_amount = 0f ;//计数多少薪资总和
        Float salary_max_amount = 0f ;//计数多少薪资总和
        Float salary_min_mean = 0f;//平均薪资
        Float salary_max_mean = 0f;//平均薪资
        String measure = null;//工资单位
        for (Text value : values) {
            String[] salary = value.toString().split("-");
            if (salary.length == 2) {//薪资长度为2
                //去掉薪资中的xx k 等字符
                String regEx = "[^0-9.]";
                Pattern pattern = Pattern.compile(regEx);
                String salary_min = pattern.matcher(salary[0]).replaceAll("").trim();//最低薪资
                String salary_max = pattern.matcher(salary[1]).replaceAll("").trim();//最高薪资  // 20k-40k---0.2-1.1万/月
                measure = salary[1];//单位

                Float min_s = Float.parseFloat(salary_min);
                Float max_s = Float.parseFloat(salary_max);
                if (measure.contains("万")  || measure.contains("千") || measure.contains("k")&& (min_s < max_s)) {//只处理以千和万结尾的数据

                    if (measure.contains("万/月")) {//以万计算
                        min_s = Float.parseFloat(salary_min) * 10;
                        max_s = Float.parseFloat(salary_max) * 10;
                    }
                    else if (measure.contains("万/年")){
                        min_s = Float.parseFloat(salary_min) /12;
                        max_s = Float.parseFloat(salary_max) / 12;
                    }
                    //最低薪资比最高薪资低，以k计算
                    salary_min_amount += min_s;//职位最低薪资总和
                    salary_max_amount += max_s;//职位最高薪资总和
                    count++;
                }
            }

        }
        salary_min_mean = salary_min_amount/ count;//最低薪资平均
        salary_max_mean = salary_max_amount/ count;//最高薪资平均

        String salary_min = String.format("%.2f", salary_min_mean).toString();//保留两位小数
        String salary_max = String.format("%.2f", salary_max_mean).toString();

        float min = Float.parseFloat(salary_min);
        float max = Float.parseFloat(salary_max);

        context.write(key, new Text("count:" + count + "\tsalary_min:" + salary_min + "\tsalary_max:" + salary_max + "\tavg_salary:" +(min+max)/2.0));//key为城市，value为总数，薪资等数据
        //System.out.println((key.toString() + "count:" + count + "\tsalary_min:" + salary_min + "\tsalary_max:" + salary_max));//控制台打印context里存的数据
    }
}
