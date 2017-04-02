#!/usr/bin/env Rscript
library(RCurl)
library(ggplot2)
library(scales)
library(data.table)
library(zoo)

#------GET FITBIT DATA--------

fitbit = read.csv("data/all-dailies.csv", head=F, stringsAsFactors=FALSE)
setnames(fitbit,names(fitbit), c("Date", "Time", "Steps"))

fitbit$Date = as.Date(fitbit$Date,format="%Y-%m-%d")

fitbit$Time = as.Date(fitbit$Time,format="%H:%M:%S")

fitbit = data.table(fitbit)[,list(Steps=sum(Steps)), by=c("Date")]

fitbit$Steps[fitbit$Steps == 0] = NA

#-----GET WEIGHT weight------

spreadsheet <- getURL("https://docs.google.com/spreadsheets/d/16LpbP9Uu0lPS-_3WfPosIvb9YJIHzUK6RolntThbJrk/export?gid=1114727413&format=csv")
weight <- read.csv(textConnection(spreadsheet), head = T, na.strings=c("-"), stringsAsFactors=FALSE)
weight <- data.table(weight)
diff_am_pm = weight$Weight.PM - weight$Weight.AM

avg_diff_am_pm = mean(diff_am_pm[complete.cases(diff_am_pm)])

weight$Weight.AM[is.na(weight$Weight.AM)] = weight$Weight.PM[is.na(weight$Weight.AM)] - avg_diff_am_pm
weight$Weight.PM[is.na(weight$Weight.PM)] = weight$Weight.AM[is.na(weight$Weight.PM)] + avg_diff_am_pm
weight$Mean.Weight = rowMeans(subset(weight, select = c(Weight.AM, Weight.AM)))

weight$Date <- as.Date(weight$Date , "%d/%m/%Y");

weight$Diff.Mean.Weight <- c(NA,
                      tail(weight$Mean.Weight, length(weight$Mean.Weight) - 1) -
                      head(weight$Mean.Weight, length(weight$Mean.Weight) - 1))

complete_means = weight$Mean.Weight[complete.cases(weight$Mean.Weight)]

current_weight = round(mean(tail(complete_means, 7)), 1)

#------JOIN TABLES-----
data = merge(fitbit, weight, by="Date")

data$Week.Label = format(data$Date, "%Y-W%m")
data = data[,lapply(.SD,mean, na.rm = TRUE),by="Week.Label"]

#------PLOT GRAPH------

plot = ggplot(data, aes(x = Steps, y = Diff.Mean.Weight)) +
       stat_smooth(method = "lm") +
       geom_point();

slp = lm(Diff.Mean.Weight ~ Steps, data = data)

print(summary(slp))

print(plot)