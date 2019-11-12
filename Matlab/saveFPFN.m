function F= saveFPFN()

n_iter = 300;

FP3 = zeros(1,n_iter); % Memory preallocation
for i=0:99     
   FP3(i+1) = FalsePositiveGBF(i,3,365);
end
for i=101:300
    m=(i-100)*100;     
   FP3(i) = FalsePositiveGBF(m,3,365);
end

FN3 = zeros(1,n_iter); % Memory preallocation
for i=0:99     
   FN3(i+1) = FalseNegativeGBF(i,3,365);
end
for i=101:300
    m=(i-100)*100;
    FN3(i) = FalseNegativeGBF(m,3,365);
end


FP5 = zeros(1,n_iter); % Memory preallocation
for i=0:99     
   FP5(i+1) = FalsePositiveGBF(i,5,565);
end
for i=101:300
    m=(i-100)*100;
    FP5(i) = FalsePositiveGBF(m,5,365);
end

FN5 = zeros(1,n_iter); % Memory preallocation
for i=0:99     
   FN5(i+1) = FalseNegativeGBF(i,5,365);
end
for i=101:300
    m=(i-100)*100;
    FN5(i) = FalseNegativeGBF(m,5,365);
end


FP10 = zeros(1,n_iter); % Memory preallocation
for i=0:99     
   FP10(i+1) = FalsePositiveGBF(i,10,365);
end
for i=101:300
    m=(i-100)*100;
    FP10(i) = FalsePositiveGBF(m,10,365);
end

FN10 = zeros(1,n_iter); % Memory preallocation
for i=0:99     
   FN10(i+1) = FalseNegativeGBF(i,10,365);
end
for i=101:300
    m=(i-100)*100;
    FN10(i) = FalseNegativeGBF(m,10,365);
end

m=[(0:99),(100:100:20000)];
F=[m',FP3', FN3',FP5',FN5',FP10',FN10'];

scatter(m,FN3);
hold
scatter(m,FP3);
scatter(m,FN5);
scatter(m,FP5);
scatter(m,FN10);
scatter(m,FP10);

