function FP= FalsePositiveGBF(m,k,n)
k0=k/2;
k1=k-k0;
q0= (1 - exp(-k0 ./ m));
q1= (1 - exp(-k1 ./ m)).* exp(-k0 ./ m); 

p0=1;	
pn=p0 .*(1-q0-q1) .^n +  (q0/(q0+q1)) .* (1- ( (1-q0-q1).^n) );
b0= m.*q0;
b1=m.*q1;
FP= (pn .^ b0).* (( 1- pn).^ b1);
end
			