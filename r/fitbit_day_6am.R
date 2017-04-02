library(ggplot2)
library(scales)
library(data.table)

all_dailies = read.csv("fitbit_daily_data/all-dailies.csv", head=F)

all_dailies$V1 = as.POSIXct(all_dailies$V1,format="%Y-%m-%d")

all_dailies$V2 = as.POSIXct(all_dailies$V2,format="%H:%M:%S")

all_dailies = data.table(all_dailies)[,list(V3=mean(V3)), by=c("V2")]

names(all_dailies) <- c("date", "value")

starting_point = (nrow(all_dailies) / 24) * 6 + 15

all_dailies$date[1:starting_point] <- all_dailies$date[1:starting_point] + 24*60*60

plot = ggplot(all_dailies, aes(x = date, y = value)) +
  geom_line(colour = "#56B4E9") +
  scale_x_datetime(labels = date_format("%H:%M"),breaks = "2 hour")+
  stat_smooth(method = "lm", formula = y ~ poly(x, 6), size = 1, se = FALSE, colour = "#D55E00") +
  xlab("time of day") + ylab("avg steps / min");

print(plot)