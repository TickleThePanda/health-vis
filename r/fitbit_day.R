library(ggplot2)
library(scales)
library(data.table)

all_dailies = read.csv("~/.fitbit/all-intraday.csv", head=F)

all_dailies$V1 = as.POSIXct(all_dailies$V1,format="%Y-%m-%d")

all_dailies$V2 = as.POSIXct(all_dailies$V2,format="%H:%M:%S")

all_dailies = data.table(all_dailies)[,list(V3=mean(V3)), by=c("V2")]

plot = ggplot(all_dailies, aes(x = V2, y = V3)) +
  geom_line(colour = "#56B4E9") +
  scale_x_datetime(labels = date_format("%H:%M"),breaks = "2 hour")+
  stat_smooth(method = "lm", formula = y ~ poly(x, 5), size = 1, se = FALSE, colour = "#D55E00") +
  xlab("time of day") + ylab("avg steps / min");

ggsave(filename = "/tmp/fitbit-day-plot.png",
       plot = plot,
       scale = 1,
       width = 12, height = 7, dpi = 100)
print(plot)